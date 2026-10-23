package model.files.mediaFiles;

import i18n.Messages;
import model.files.common.JsonFile;
import model.files.locationModels.LocationModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This is my files holder singleton implementation for holding the single ref point to files_in.json,
 * and it's also used to parse and return the file objects from the file.
 *
 * @author RazvanNuta
 * @version 2
 * @see JsonFile
 * @see MyFileModel
 * @since 2026.10.18
 */
public final class MyFilesHolder extends JsonFile<MyFileModel> {
    private static final String FILES_IN_LOCATION = "test-data-files\\files_in.json";
    private static final String FILES_OUT_LOCATION = "test-data-files\\files_out.json";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    private static final Messages messages = Messages.getInstance();
    private static MyFilesHolder instance;
    private final String FILES_IN_INVALID_EXAMPLE_LOCATION = "test-data-files\\files_in_invalid_example.json";
    private HashMap<Integer, LocationModel> locationsMap;

    private MyFilesHolder(String inputFileName, String outputFileName) throws FileNotFoundException {
        super(inputFileName, outputFileName);
    }


    public static MyFilesHolder GetInstance() {
        if (instance == null) {
            try {
                instance = new MyFilesHolder(MyFilesHolder.FILES_IN_LOCATION, MyFilesHolder.FILES_OUT_LOCATION);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public HashMap<Integer, MyFileModel> ParseFile(HashMap<Integer, LocationModel> locationsMap) {
        this.locationsMap = locationsMap;
        return ParseFile();
    }

    @Override
    protected List<MyFileModel> parseJSON(String jsonStr) {
        List<MyFileModel> myFileList = new ArrayList<>();

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

            MyFileModel mf = new MyFileModel(id, name, path, locationId, extension, type.name(), sizeBytes, lastModified);
            myFileList.add(mf);
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