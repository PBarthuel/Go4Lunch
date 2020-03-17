package paul.barthuel.go4lunch.ui.map_view;

public class LunchMarker {

    private double latitude;
    private double longitude;
    private String name;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LunchMarker(double latitude, double longitude, String name) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }
}
