package model.files.locationModels;

import model.files.common.JsonFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
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
    private static final String LOCATIONS_IN_LOCATION = "test-data-files\\locations_in.json";
    private static final String LOCATIONS_OUT_LOCATION = "test-data-files\\locations_out.json";
    private static MyLocationsHolder instance;

    private MyLocationsHolder(String inputFileName, String outputFileName) {
        super(inputFileName, outputFileName);
    }

    public static MyLocationsHolder GetInstance() {
        if (instance == null) {
            instance = new MyLocationsHolder(MyLocationsHolder.LOCATIONS_IN_LOCATION, MyLocationsHolder.LOCATIONS_OUT_LOCATION);
        }
        return instance;
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
        List<LocationModel> subLocations = new ArrayList<>();
        for (LocationModel mf : myFileList) {
            subLocations.addAll(mf.getSubLocations());
        }

        JSONArray array = new JSONArray();
        for (LocationModel mf : myFileList) {
            if (!subLocations.contains(mf)) {
                array.put(toJSONObject(mf));
            }
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