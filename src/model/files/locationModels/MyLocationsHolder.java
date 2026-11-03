package model.files.locationModels;

import exceptions.DuplicateLocationException;
import exceptions.InvalidLocationException;
import model.files.common.JsonFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

enum LocationType {
    SYSTEM,
    USER_CREATED
}

/**
 * This is my locations holder singleton implementation for holding the single ref point to locations_in.json,
 * and it's also used to parse and return the locations from the file.
 *
 * @author RazvanNuta
 * @version 2
 * @see model.files.common.JsonFile
 * @see LocationModel
 * @see LocationType
 * @since 2026.10.18
 */
public final class MyLocationsHolder extends JsonFile<LocationModel> {
    private static final String LOCATIONS_LOCATION = "test-data-files\\locations.json";
    private static final String DRIVE_PATTERN = "[A-Za-z]:\\\\";
    private static final String PATH_PATTERN = DRIVE_PATTERN + ".*";
    private static final String INVALID_CHARACTERS = ":*?\"<>|";
    private static final int DRIVE_LENGTH = 3;
    private static MyLocationsHolder instance;
    private HashMap<Integer, LocationModel> locations;

    private MyLocationsHolder(String inputFileName, String outputFileName) {
        super(inputFileName, outputFileName);
    }

    public static MyLocationsHolder GetInstance() {
        if (instance == null) {
            instance = new MyLocationsHolder(MyLocationsHolder.LOCATIONS_LOCATION, MyLocationsHolder.LOCATIONS_LOCATION);
        }
        return instance;
    }

    @Override
    public HashMap<Integer, LocationModel> ParseFile() {
        locations = super.ParseFile();
        return locations;
    }

    public HashMap<Integer, LocationModel> getLocations() {
        return locations;
    }

    public List<LocationModel> getRootLocations(Collection<LocationModel> allLocations) {
        List<LocationModel> subLocations = new ArrayList<>();
        for (LocationModel location : allLocations) {
            subLocations.addAll(location.getSubLocations());
        }

        List<LocationModel> rootLocations = new ArrayList<>();
        for (LocationModel location : allLocations) {
            if (!subLocations.contains(location)) {
                rootLocations.add(location);
            }
        }
        return rootLocations;
    }

    public String validatePath(String path) throws InvalidLocationException, DuplicateLocationException {
        if (path.isEmpty()) {
            throw new InvalidLocationException("label.error.location.empty-path");
        }

        String normalizedPath = normalizePath(path);
        if (!isValidPath(normalizedPath)) {
            throw new InvalidLocationException("label.error.location.invalid-path");
        }
        if (findByPath(normalizedPath) != null) {
            throw new DuplicateLocationException("label.error.location.duplicate");
        }
        if (!isDrive(normalizedPath) && findParent(normalizedPath) == null) {
            throw new InvalidLocationException("label.error.location.parent-not-found");
        }
        return normalizedPath;
    }

    public LocationModel addLocation(String path, String name) throws InvalidLocationException {
        if (name.isEmpty()) {
            throw new InvalidLocationException("label.error.location.empty-name");
        }

        LocationModel location = new LocationModel(generateId(), name, path, LocationType.USER_CREATED);
        LocationModel parent = findParent(path);
        List<LocationModel> siblings = parent != null ? parent.getSubLocations() : getRootLocations(locations.values());
        for (LocationModel sibling : new ArrayList<>(siblings)) {
            if (isSubfolder(sibling.getPath(), path)) {
                siblings.remove(sibling);
                location.getSubLocations().add(sibling);
            }
        }

        if (parent != null) {
            parent.getSubLocations().add(location);
        }
        locations.put(location.getId(), location);
        return location;
    }

    private String normalizePath(String path) {
        String normalizedPath = path.replace('/', '\\');
        while (normalizedPath.length() > DRIVE_LENGTH && normalizedPath.endsWith("\\")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }

    private boolean isValidPath(String path) {
        if (!path.matches(PATH_PATTERN)) {
            return false;
        }
        if (isDrive(path)) {
            return true;
        }

        for (String folder : path.substring(DRIVE_LENGTH).split("\\\\")) {
            if (folder.isEmpty() || folder.equals(".") || folder.equals("..")) {
                return false;
            }
            for (char character : INVALID_CHARACTERS.toCharArray()) {
                if (folder.indexOf(character) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isDrive(String path) {
        return path.matches(DRIVE_PATTERN);
    }

    private String getParentPath(String path) {
        int separatorIndex = path.lastIndexOf('\\');
        if (isDrive(path) || separatorIndex < 0) {
            return null;
        }
        if (separatorIndex == DRIVE_LENGTH - 1 && isDrive(path.substring(0, DRIVE_LENGTH))) {
            return path.substring(0, DRIVE_LENGTH);
        }
        return path.substring(0, separatorIndex);
    }

    private LocationModel findByPath(String path) {
        for (LocationModel location : locations.values()) {
            if (normalizePath(location.getPath()).equalsIgnoreCase(path)) {
                return location;
            }
        }
        return null;
    }

    private LocationModel findParent(String path) {
        String parentPath = getParentPath(path);
        while (parentPath != null) {
            LocationModel location = findByPath(parentPath);
            if (location != null) {
                return location;
            }
            parentPath = getParentPath(parentPath);
        }
        return null;
    }

    private boolean isSubfolder(String path, String folderPath) {
        String parentPath = getParentPath(normalizePath(path));
        while (parentPath != null) {
            if (parentPath.equalsIgnoreCase(folderPath)) {
                return true;
            }
            parentPath = getParentPath(parentPath);
        }
        return false;
    }

    private int generateId() {
        int maxId = 0;
        for (int id : locations.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    @Override
    protected List<LocationModel> parseJSON(String jsonStr) {
        List<LocationModel> locationModelList = new ArrayList<>();

        JSONArray array = new JSONArray(jsonStr);
        parseLocations(array, locationModelList, null);

        return locationModelList;
    }

    private void parseLocations(JSONArray array, List<LocationModel> locationModelList, LocationModel parent) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String path = obj.getString("path");

            LocationModel tr = new LocationModel(id, name, path, LocationType.SYSTEM);
            locationModelList.add(tr);
            if (parent != null) {
                parent.getSubLocations().add(tr);
            }

            if (obj.has("subLocations")) {
                parseLocations(obj.getJSONArray("subLocations"), locationModelList, tr);
            }
        }
    }

    @Override
    protected String toJSON(Collection<LocationModel> myFileList) {
        JSONArray array = new JSONArray();
        for (LocationModel mf : getRootLocations(myFileList)) {
            array.put(toJSONObject(mf));
        }
        return array.toString();
    }

    private JSONObject toJSONObject(LocationModel mf) {
        JSONObject obj = new JSONObject();
        obj.put("id", mf.getId());
        obj.put("name", mf.getName());
        obj.put("path", mf.getPath());
        obj.put("locationType", mf.getLocationType());

        JSONArray subLocations = new JSONArray();
        for (LocationModel subLocation : mf.getSubLocations()) {
            subLocations.put(toJSONObject(subLocation));
        }
        obj.put("subLocations", subLocations);
        return obj;
    }
}