package paul.barthuel.go4lunch.ui.list_view;

import androidx.annotation.ColorRes;

public class RestaurantInfo {

    private final String name;
    private final String address;
    private final String openingHours;
    private final String distance;
    private final Double rating;
    private final String image;
    private final String id;
    private final String attendies;
    private final boolean isAttendiesVisible;
    private final int backgroundColor;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getDistance() {
        return distance;
    }

    public Double getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getAttendies( ) { return attendies; }

    public boolean isAttendiesVisible() {
        return isAttendiesVisible;
    }

    public int getBackgroundColor() { return backgroundColor; }


    public RestaurantInfo(String name,
                          String address,
                          String openingHours,
                          String distance,
                          Double rating,
                          String image,
                          String id,
                          String attendies,
                          boolean isAttendiesVisible,
                          @ColorRes int backgroundColor) {

        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.distance = distance;
        this.rating = rating;
        this.image = image;
        this.id = id;
        this.attendies = attendies;
        this.isAttendiesVisible = isAttendiesVisible;
        this.backgroundColor = backgroundColor;
    }

}
