package model.files.locationModels;

import exceptions.DataFileException;
import exceptions.DuplicateLocationException;
import exceptions.InvalidLocationException;
import exceptions.LocationNotFoundException;
import i18n.Messages;
import model.files.common.JsonFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This is my locations holder singleton implementation for holding the single ref point to locations.json,
 * and it's also used to parse and return the locations from the file.
 *
 * @author RazvanNuta
 * @version 2
 * @see model.files.common.JsonFile
 * @see LocationModel
 * @see LocationType
 */
public final class MyLocationsHolder extends JsonFile<LocationModel> {
    private static final String LOCATIONS_LOCATION = "test-data-files\\locations.json";
    private static final String DRIVE_PATTERN = "[A-Za-z]:\\\\";
    private static final String PATH_PATTERN = DRIVE_PATTERN + ".*";
    private static final String INVALID_CHARACTERS = ":*?\"<>|";
    private static final int DRIVE_LENGTH = 3;
    private static final Messages messages = Messages.getInstance();
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
    public HashMap<Integer, LocationModel> ParseFile() throws DataFileException {
        locations = super.ParseFile();
        return locations;
    }

    public HashMap<Integer, LocationModel> getLocations() {
        return locations;
    }

    /**
     * This method is used to find the root locations, the ones that are not a sub location of any other location.
     * The locations are shown in the menu and saved in locations.json starting from these roots,
     * with their sub locations inside them.
     *
     * @param allLocations All the locations, including the sub locations
     * @return The locations that are not a sub location of another location
     */
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

    /**
     * This method is used to validate the path of a new location, before the name of the location is read from the console.
     * The path is normalized first ("/" becomes "\" and the "\" from the end is removed). Then it must have a valid format,
     * starting with a drive (for example C:\), it must not be monitored already and, if it is not a drive,
     * one of its parent folders must be monitored.
     *
     * @param path The path of the new location, as it was read from the console
     * @return The normalized path, that will be the path of the new location
     * @throws InvalidLocationException In the case the path is empty, has an invalid format
     * or none of its parent folders is monitored throws this exception.
     * @throws DuplicateLocationException In the case a location with the same path is already monitored throws this exception.
     * @see #addLocation(String, String)
     */
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

    /**
     * This method is used to add a new location created by the user, in memory, under its closest monitored parent folder
     * (a drive is added as a root location). The locations that are already monitored inside the new path
     * become sub locations of the new location, similar to a real desktop.
     * The new location receives the biggest id + 1 and the USER_CREATED type.
     *
     * @param path The path of the new location, already validated and normalized by validatePath
     * @param name The name of the new location
     * @return The added location, with the sub locations that were moved under it
     * @throws InvalidLocationException In the case the name is empty throws this exception.
     * @see #validatePath(String)
     * @see LocationType
     */
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

    public List<LocationModel> findByNameOrId(String nameOrId) throws InvalidLocationException, LocationNotFoundException {
        if (nameOrId.isEmpty()) {
            throw new InvalidLocationException("label.error.location.empty-name-or-id");
        }

        List<LocationModel> matches = new ArrayList<>();
        for (LocationModel location : locations.values()) {
            if (location.getName().equalsIgnoreCase(nameOrId) || String.valueOf(location.getId()).equals(nameOrId)) {
                matches.add(location);
            }
        }
        if (matches.isEmpty()) {
            throw new LocationNotFoundException("label.error.location.not-found");
        }
        return matches;
    }

    public LocationModel findLocationByPath(String path) throws InvalidLocationException, LocationNotFoundException {
        if (path.isEmpty()) {
            throw new InvalidLocationException("label.error.location.empty-path");
        }

        String normalizedPath = normalizePath(path);
        if (!isValidPath(normalizedPath)) {
            throw new InvalidLocationException("label.error.location.invalid-path");
        }

        LocationModel location = findByPath(normalizedPath);
        if (location == null) {
            throw new LocationNotFoundException("label.error.move.location-not-found");
        }
        return location;
    }

    /**
     * This method is used to delete a location from memory, together with all its sub locations.
     * The returned ids are used after that to remove the media files of the deleted locations.
     *
     * @param location The location to be deleted, chosen by name or id from the menu
     * @return The ids of the deleted location and of all its sub locations
     * @see model.files.mediaFiles.MyFilesHolder#removeFilesOfLocations(List)
     */
    public List<Integer> deleteLocation(LocationModel location) {
        for (LocationModel parent : locations.values()) {
            parent.getSubLocations().remove(location);
        }

        List<Integer> deletedIds = new ArrayList<>();
        removeWithSubLocations(location, deletedIds);
        return deletedIds;
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

    private void removeWithSubLocations(LocationModel location, List<Integer> deletedIds) {
        locations.remove(location.getId());
        deletedIds.add(location.getId());
        for (LocationModel subLocation : location.getSubLocations()) {
            removeWithSubLocations(subLocation, deletedIds);
        }
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
            JSONObject obj = null;
            String name = null;
            try {
                obj = array.getJSONObject(i);
                name = obj.getString("name");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.location.ignored.unreadable") + ": " + (i + 1));
                continue;
            }

            int id = 0;
            try {
                id = obj.getInt("id");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.location.ignored.invalid-id") + ": " + name);
                continue;
            }

            String path = null;
            try {
                path = obj.getString("path");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.location.ignored.invalid-path") + ": " + name);
                continue;
            }

            JSONArray subLocations = new JSONArray();
            try {
                if (obj.has("subLocations")) {
                    subLocations = obj.getJSONArray("subLocations");
                }
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.location.ignored.invalid-sublocations") + ": " + name);
                continue;
            }

            try {
                validateLocation(id, name, path, parent, locationModelList);
            } catch (InvalidLocationException | DuplicateLocationException e) {
                System.out.println(messages.get(e.getMessage()) + ": " + name);
                continue;
            }

            LocationType locationType = obj.optEnum(LocationType.class, "locationType", LocationType.SYSTEM);
            LocationModel tr = new LocationModel(id, name, path, locationType);
            locationModelList.add(tr);
            if (parent != null) {
                parent.getSubLocations().add(tr);
            }

            parseLocations(subLocations, locationModelList, tr);
        }
    }

    private void validateLocation(int id, String name, String path, LocationModel parent, List<LocationModel> acceptedLocations) throws InvalidLocationException, DuplicateLocationException {
        if (name.trim().isEmpty()) {
            throw new InvalidLocationException("label.error.location.ignored.empty-name");
        }

        String normalizedPath = normalizePath(path);
        if (!isValidPath(normalizedPath)) {
            throw new InvalidLocationException("label.error.location.ignored.invalid-path");
        }
        if (parent == null && !isDrive(normalizedPath)) {
            throw new InvalidLocationException("label.error.location.ignored.root-not-drive");
        }
        if (parent != null && !isSubfolder(normalizedPath, normalizePath(parent.getPath()))) {
            throw new InvalidLocationException("label.error.location.ignored.outside-parent");
        }

        for (LocationModel location : acceptedLocations) {
            if (location.getId() == id) {
                throw new DuplicateLocationException("label.error.location.ignored.duplicate-id");
            }
        }
        for (LocationModel location : acceptedLocations) {
            if (normalizePath(location.getPath()).equalsIgnoreCase(normalizedPath)) {
                throw new DuplicateLocationException("label.error.location.ignored.duplicate-path");
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