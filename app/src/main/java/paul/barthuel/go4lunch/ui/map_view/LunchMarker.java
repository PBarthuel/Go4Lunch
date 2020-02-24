package paul.barthuel.go4lunch.ui.map_view;

class LunchMarker {

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LunchMarker(double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;

}
