package paul.barthuel.go4lunch.ui.map_view;

import androidx.annotation.ColorRes;

public class LunchMarker {

    private final double latitude;
    private final double longitude;
    private final String name;
    private final int backgroundColor;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getBackGroundColor() { return backgroundColor; }

    public LunchMarker(double latitude,
                       double longitude,
                       String name,
                       @ColorRes int backgroundColor) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.backgroundColor = backgroundColor;
    }
}
