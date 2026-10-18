package model.files.LocationModels;

import model.files.JsonFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

enum LocationType{
    SYSTEM,
    USER_CREATED
}

/**
 * This is my locations file singletone implementation for holding the single ref point to locations_in.json,
 * and it's also used to parse and return the locations from the file.
 * @author RazvanNuta
 * @since 2026.10.18
 */
public final class MyLocationsInFile extends JsonFile<Location> {
    private static final String LOCATIONS_IN_LOCATION = "test-data-files\\locations_in.json";
    private static MyLocationsInFile instance;

    private MyLocationsInFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public static MyLocationsInFile GetInstance(){
        if(instance == null){
            try {
                instance = new MyLocationsInFile(MyLocationsInFile.LOCATIONS_IN_LOCATION);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public HashMap<Integer, Location> ParseFile() {
        HashMap<Integer, Location> locations = new HashMap<>();
        String linie = "";
        String jsonStr = "";

        try {
            BufferedReader br = GetBR();

            while((linie = br.readLine()) != null){
                jsonStr = jsonStr + linie;
            }

            List<Location> locationList = parsareJSON(jsonStr);
            locationList.forEach(location -> {
                if(!locations.containsKey(location.getId())){
                    locations.put(location.getId(), location);
                }
            });

            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return locations;
    }

    private  List<Location> parsareJSON(String jsonStr){
        List<Location> locationList = new ArrayList<>();

        JSONArray array = new JSONArray(jsonStr);
        for (int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String path = obj.getString("path");

            Location tr = new Location(id, name, path, LocationType.SYSTEM);
            locationList.add(tr);
        }

        return locationList;
    }
}