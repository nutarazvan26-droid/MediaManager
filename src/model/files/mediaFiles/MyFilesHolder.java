package model.files.mediaFiles;

import exceptions.DataFileException;
import exceptions.DuplicateMediaFileException;
import exceptions.InvalidFilterException;
import exceptions.InvalidLocationException;
import exceptions.InvalidMediaFileException;
import exceptions.MediaFileNotFoundException;
import i18n.Messages;
import model.files.common.JsonFile;
import model.files.locationModels.LocationModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * This is my files holder singleton implementation for holding the single ref point to files.json,
 * and it's also used to parse and return the file objects from the file.
 *
 * @author RazvanNuta
 * @version 2
 * @see JsonFile
 * @see MyFileModel
 */
public final class MyFilesHolder extends JsonFile<MyFileModel> {
    private static final String FILES_LOCATION = "test-data-files\\files.json";
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    private static final Messages messages = Messages.getInstance();
    private static MyFilesHolder instance;
    private HashMap<Integer, LocationModel> locationsMap;
    private HashMap<Integer, MyFileModel> files;

    private MyFilesHolder(String inputFileName, String outputFileName) {
        super(inputFileName, outputFileName);
    }

    public static MyFilesHolder GetInstance() {
        if (instance == null) {
            instance = new MyFilesHolder(MyFilesHolder.FILES_LOCATION, MyFilesHolder.FILES_LOCATION);
        }
        return instance;
    }

    /**
     * This method is used to parse the media files from files.json, after the locations were parsed from locations.json.
     * The locations are needed to check that each media file belongs to a monitored location
     * and that its path is the path of that location followed by its name.
     *
     * @param locationsMap The locations parsed from locations.json, by id
     * @return The valid media files from files.json, by id
     * @throws DataFileException In the case files.json cannot be read or it does not contain a valid JSON throws this exception.
     * @see JsonFile#ParseFile()
     * @see model.files.locationModels.MyLocationsHolder
     */
    public HashMap<Integer, MyFileModel> ParseFile(HashMap<Integer, LocationModel> locationsMap) throws DataFileException {
        this.locationsMap = locationsMap;
        files = ParseFile();
        return files;
    }

    public HashMap<Integer, MyFileModel> getFiles() {
        return files;
    }

    public int removeFilesOfLocations(List<Integer> locationIds) {
        int filesCount = files.size();
        files.values().removeIf(file -> locationIds.contains(file.getLocationId()));
        return filesCount - files.size();
    }

    public List<MyFileModel> findByNameOrId(String nameOrId) throws InvalidMediaFileException, MediaFileNotFoundException {
        if (nameOrId.isEmpty()) {
            throw new InvalidMediaFileException("label.error.move.empty-name-or-id");
        }

        List<MyFileModel> matches = new ArrayList<>();
        for (MyFileModel file : files.values()) {
            if (file.getName().equalsIgnoreCase(nameOrId) || String.valueOf(file.getId()).equals(nameOrId)) {
                matches.add(file);
            }
        }
        if (matches.isEmpty()) {
            throw new MediaFileNotFoundException("label.error.move.file-not-found");
        }
        return matches;
    }

    public void moveFile(MyFileModel file, LocationModel location) throws InvalidLocationException, DuplicateMediaFileException {
        if (file.getLocationId() == location.getId()) {
            throw new InvalidLocationException("label.error.move.same-location");
        }
        for (MyFileModel other : files.values()) {
            if (other.getLocationId() == location.getId() && other.getName().equalsIgnoreCase(file.getName())) {
                throw new DuplicateMediaFileException("label.error.move.duplicate-name");
            }
        }

        file.setLocationId(location.getId());
        file.setPath(buildPath(location, file.getName()));
    }

    private String buildPath(LocationModel location, String name) {
        String locationPath = location.getPath();
        return locationPath.endsWith("\\") ? locationPath + name : locationPath + "\\" + name;
    }

    public List<MyFileModel> filterFiles(FileFilterType filterType, String value) throws InvalidFilterException {
        String filterValue = value;
        if (filterType == FileFilterType.EXTENSION) {
            filterValue = validateExtension(value);
        } else if (filterType == FileFilterType.NAME && value.isEmpty()) {
            throw new InvalidFilterException("label.error.filter.empty-name");
        }

        List<MyFileModel> filteredFiles = new ArrayList<>();
        for (MyFileModel file : files.values()) {
            if (filterType.matches(file, filterValue)) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    private String validateExtension(String extension) throws InvalidFilterException {
        if (extension.isEmpty()) {
            throw new InvalidFilterException("label.error.filter.empty-extension");
        }

        String normalizedExtension = extension.startsWith(".") ? extension.substring(1) : extension;
        if (MediaType.fromExtension(normalizedExtension) == null) {
            throw new InvalidFilterException("label.error.filter.unsupported-extension");
        }
        return normalizedExtension;
    }

    public int[] countFilesByType() {
        int[] filesCount = new int[MediaType.values().length];
        for (MyFileModel file : files.values()) {
            filesCount[file.getType().ordinal()]++;
        }
        return filesCount;
    }

    public long[] sumSizesByType() {
        long[] sizes = new long[MediaType.values().length];
        for (MyFileModel file : files.values()) {
            sizes[file.getType().ordinal()] += file.getSizeBytes();
        }
        return sizes;
    }

    public int[] countFilesByExtension() {
        List<String> extensions = MediaType.getAllExtensions();
        int[] filesCount = new int[extensions.size()];
        for (MyFileModel file : files.values()) {
            filesCount[extensions.indexOf(file.getExtension().toLowerCase(Locale.ROOT))]++;
        }
        return filesCount;
    }

    public MyFileModel findLargestFile() {
        MyFileModel largestFile = null;
        for (MyFileModel file : files.values()) {
            if (largestFile == null || file.getSizeBytes() > largestFile.getSizeBytes()) {
                largestFile = file;
            }
        }
        return largestFile;
    }

    /**
     * This method is used to parse the media files from the content of files.json and to validate each one of them.
     * A media file with a missing or invalid field is ignored and a message is printed, for example when its id is duplicated,
     * its location is not monitored, its extension is not supported or does not match its name and type, its size is negative,
     * its date is not in the dd.MM.yyyy HH:mm format or its path is not the path of its location followed by its name.
     *
     * @param jsonStr The content of files.json
     * @return The valid media files from files.json
     * @see MediaType
     */
    @Override
    protected List<MyFileModel> parseJSON(String jsonStr) {
        List<MyFileModel> myFileList = new ArrayList<>();
        HashSet<Integer> ids = new HashSet<>();

        JSONArray array = new JSONArray(jsonStr);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = null;
            String name = null;
            try {
                obj = array.getJSONObject(i);
                name = obj.getString("name");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.file.unreadable") + ": " + (i + 1));
                continue;
            }

            int id = 0;
            try {
                id = obj.getInt("id");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.file.invalid-id") + ": " + name);
                continue;
            }
            if (ids.contains(id)) {
                System.out.println(messages.get("label.error.file.duplicate-id") + ": " + name);
                continue;
            }

            int locationId = 0;
            try {
                locationId = obj.getInt("locationId");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.file.invalid-location") + ": " + name);
                continue;
            }
            if (locationsMap != null && !locationsMap.containsKey(locationId)) {
                System.out.println(messages.get("label.error.file.location-not-found") + ": " + name);
                continue;
            }

            String extension = null;
            try {
                extension = obj.getString("extension");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.file.invalid-extension") + ": " + name);
                continue;
            }
            if (MediaType.fromExtension(extension) == null) {
                System.out.println(messages.get("label.error.file.invalid-extension") + ": " + name);
                continue;
            }
            if (!name.toLowerCase(Locale.ROOT).endsWith("." + extension.toLowerCase(Locale.ROOT))) {
                System.out.println(messages.get("label.error.file.extension-mismatch") + ": " + name);
                continue;
            }

            MediaType type = null;
            try {
                type = MediaType.valueOf(obj.getString("type"));
            } catch (JSONException | IllegalArgumentException e) {
                System.out.println(messages.get("label.error.file.invalid-type") + ": " + name);
                continue;
            }
            if (type != MediaType.fromExtension(extension)) {
                System.out.println(messages.get("label.error.file.type-mismatch") + ": " + name);
                continue;
            }

            long sizeBytes = 0;
            try {
                sizeBytes = obj.getLong("sizeBytes");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.file.invalid-size") + ": " + name);
                continue;
            }
            if (sizeBytes < 0) {
                System.out.println(messages.get("label.error.file.invalid-size") + ": " + name);
                continue;
            }

            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            formatter.setLenient(false);

            Date lastModified = null;
            try {
                lastModified = formatter.parse(obj.getString("lastModified"));
            } catch (JSONException | ParseException e) {
                System.out.println(messages.get("label.error.file.invalid-date") + ": " + name);
                continue;
            }

            String path = null;
            try {
                path = obj.getString("path");
            } catch (JSONException e) {
                System.out.println(messages.get("label.error.file.invalid-path") + ": " + name);
                continue;
            }
            if (locationsMap != null && !path.equalsIgnoreCase(buildPath(locationsMap.get(locationId), name))) {
                System.out.println(messages.get("label.error.file.path-mismatch") + ": " + name);
                continue;
            }

            MyFileModel mf = new MyFileModel(id, name, path, locationId, extension, type, sizeBytes, lastModified);
            myFileList.add(mf);
            ids.add(id);
        }
        return myFileList;
    }

    @Override
    protected String toJSON(Collection<MyFileModel> myFileList) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        JSONArray array = new JSONArray();
        for (MyFileModel mf : myFileList) {
            JSONObject obj = new JSONObject();
            obj.put("id", mf.getId());
            obj.put("locationId", mf.getLocationId());
            obj.put("name", mf.getName());
            obj.put("extension", mf.getExtension());
            obj.put("type", mf.getType());
            obj.put("sizeBytes", mf.getSizeBytes());
            obj.put("lastModified", formatter.format(mf.getLastModified()));
            obj.put("path", mf.getPath());
            array.put(obj);
        }
        return array.toString();
    }
}