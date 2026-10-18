package model.files.LocationModels;

import model.files.CommonFile;

import java.util.Objects;

public class Location extends CommonFile {
    private LocationType locationType;

    public Location(int id, String name, String path, LocationType locationType) {
        super(id, name, path);
        this.locationType = locationType;
    }
    public LocationType getLocationType() {
        return locationType;
    }
    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) return false;
        if (!super.equals(o)) return false;
        Location location = (Location) o;
        return locationType == location.locationType;
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
