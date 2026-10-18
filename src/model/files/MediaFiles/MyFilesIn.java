package model.files.MediaFiles;

import model.files.JsonFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is my files in singletone implementation for holding the single ref point to files_in.json,
 * and it's also used to parse and return the file objects from the file.
 * @author RazvanNuta
 * @since 2026.10.18
 */
public final class MyFilesIn extends JsonFile<MyFile> {
    private static final String FILES_IN_LOCATION = "test-data-files\\files_in.json";
    private  final String FILES_IN_INVALID_EXAMPLE_LOCATION = "test-data-files\\files_in_invalid_example.json";

    private static MyFilesIn instance;

    private MyFilesIn(String fileName) throws FileNotFoundException {
        super(fileName);
    }


    public static MyFilesIn GetInstance(){
        if(instance == null){
            try {
                instance = new MyFilesIn(MyFilesIn.FILES_IN_LOCATION);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public HashMap<Integer, MyFile> ParseFile() {
        HashMap<Integer, MyFile> myFiles = new HashMap<>();
        String linie = "";
        String jsonStr = "";

        try {
            BufferedReader br = GetBR();

            while((linie = br.readLine()) != null){
                jsonStr = jsonStr + linie;
            }

            List<MyFile> myFilesList = parseJSON(jsonStr);
            myFilesList.forEach(file -> {
                if(!myFiles.containsKey(file.getId())){
                    myFiles.put(file.getId(), file);
                }
            });

            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return myFiles;
    }

    private List<MyFile> parseJSON(String jsonStr){
        List<MyFile> myFileList = new ArrayList<>();

        JSONArray array = new JSONArray(jsonStr);
        for (int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String path = obj.getString("path");
            int  locationId = obj.getInt("locationId");
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

            MyFile mf = new MyFile(id, name, path, locationId, extension, type, sizeBytes, lastModified );
            myFileList.add(mf);
        }

        return myFileList;
    }
}