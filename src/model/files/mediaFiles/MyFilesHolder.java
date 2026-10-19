package model.files.mediaFiles;

import model.files.common.JsonFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is my files holder singleton implementation for holding the single ref point to files_in.json,
 * and it's also used to parse and return the file objects from the file.
 * @author RazvanNuta
 * @since 2026.10.18
 * @version 2
 * @see JsonFile
 * @see MyFileModel
 */
public final class MyFilesHolder extends JsonFile<MyFileModel> {
    private static final String FILES_IN_LOCATION = "test-data-files\\files_in.json";
    private static final String FILES_OUT_LOCATION = "test-data-files\\files_out.json";
    private  final String FILES_IN_INVALID_EXAMPLE_LOCATION = "test-data-files\\files_in_invalid_example.json";

    private static MyFilesHolder instance;

    private MyFilesHolder(String inputFileName, String outputFileName) throws FileNotFoundException {
        super(inputFileName, outputFileName);
    }


    public static MyFilesHolder GetInstance(){
        if(instance == null){
            try {
                instance = new MyFilesHolder(MyFilesHolder.FILES_IN_LOCATION,  MyFilesHolder.FILES_OUT_LOCATION);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    protected List<MyFileModel> parseJSON(String jsonStr){
        List<MyFileModel> myFileList = new ArrayList<>();

        JSONArray array = new JSONArray(jsonStr);
        for (int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            int  locationId = obj.getInt("locationId");
            String name = obj.getString("name");
            String extension = obj.getString("extension");
            String type = obj.getString("type");
            long sizeBytes = obj.getLong("sizeBytes");
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            Date lastModified = null;
            try {
                lastModified = formatter.parse(obj.getString("lastModified"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String path = obj.getString("path");

            MyFileModel mf = new MyFileModel(id, name, path, locationId, extension, type, sizeBytes, lastModified );
            myFileList.add(mf);
        }
        return myFileList;
    }

    @Override
    protected String toJSON(Collection<MyFileModel> myFileList){
        JSONArray array = new JSONArray();
        for (MyFileModel mf : myFileList){
            JSONObject obj = new JSONObject();
            obj.put("id", mf.getId());
            obj.put("locationId", mf.getLocationId());
            obj.put("name", mf.getName());
            obj.put("extension", mf.getExtension());
            obj.put("type", mf.getType());
            obj.put("sizeBytes", mf.getSizeBytes());
            obj.put("lastModified", mf.getLastModified());
            obj.put("path", mf.getPath());
            array.put(obj);
        }
        return array.toString();
    }
}