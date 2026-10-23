package model.files.locationModels;

import model.files.common.CommonFileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationModel extends CommonFileModel {
    private LocationType locationType;
    private List<LocationModel> subLocations = new ArrayList<>();

    public LocationModel(int id, String name, String path, LocationType locationType) {
        super(id, name, path);
        this.locationType = locationType;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public List<LocationModel> getSubLocations() {
        return subLocations;
    }

    public void setSubLocations(List<LocationModel> subLocations) {
        this.subLocations = subLocations;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocationModel)) return false;
        if (!super.equals(o)) return false;
        LocationModel locationModel = (LocationModel) o;
        return locationType == locationModel.locationType;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationType=" + locationType +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locationType);
    }
}
