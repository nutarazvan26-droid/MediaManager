package model.files.locationModels;

import model.files.common.JsonFile;
import model.files.mediaFiles.MyFileModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

enum LocationType{
    SYSTEM,
    USER_CREATED
}

/**
 * This is my locations holder singleton implementation for holding the single ref point to locations_in.json,
 * and it's also used to parse and return the locations from the file.
 * @author RazvanNuta
 * @since 2026.10.18
 * @see model.files.common.JsonFile
 * @see LocationModel
 * @see LocationType
 * @version 2
 */
public final class MyLocationsHolder extends JsonFile<LocationModel> {
    private static final String LOCATIONS_IN_LOCATION = "test-data-files\\locations_in.json";
    private static final String LOCATIONS_OUT_LOCATION = "test-data-files\\locations_out.json";
    private static MyLocationsHolder instance;

    private MyLocationsHolder(String inputFileName, String outputFileName)  {
        super(inputFileName,  outputFileName);
    }

    public static MyLocationsHolder GetInstance(){
        if(instance == null){
            instance = new MyLocationsHolder(MyLocationsHolder.LOCATIONS_IN_LOCATION,  MyLocationsHolder.LOCATIONS_OUT_LOCATION);
        }
        return instance;
    }

    @Override
    protected List<LocationModel> parseJSON(String jsonStr){
        List<LocationModel> locationModelList = new ArrayList<>();

        JSONArray array = new JSONArray(jsonStr);
        for (int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String path = obj.getString("path");

            LocationModel tr = new LocationModel(id, name, path, LocationType.SYSTEM);
            locationModelList.add(tr);
        }

        return locationModelList;
    }
    @Override
    protected String toJSON(Collection<LocationModel> myFileList){
        JSONArray array = new JSONArray();
        for (LocationModel mf : myFileList){
            JSONObject obj = new JSONObject();
            obj.put("id", mf.getId());
            obj.put("name", mf.getName());
            obj.put("path", mf.getPath());
            obj.put("locationType", mf.getLocationType());
            array.put(obj);
        }
        return array.toString();
    }
}