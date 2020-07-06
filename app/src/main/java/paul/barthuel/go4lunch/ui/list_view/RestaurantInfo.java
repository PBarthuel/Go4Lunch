package paul.barthuel.go4lunch.ui.list_view;

public class RestaurantInfo {

    private String name;
    private String address;
    private String openingHours;
    private String distance;
    private Double rating;
    private String image;
    private String id;
    private String attendies;
    private boolean isAttendiesVisible;

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


    public RestaurantInfo(String name,
                          String address,
                          String openingHours,
                          String distance,
                          Double rating,
                          String image,
                          String id,
                          String attendies,
                          boolean isAttendiesVisible) {

        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.distance = distance;
        this.rating = rating;
        this.image = image;
        this.id = id;
        this.attendies = attendies;
        this.isAttendiesVisible = isAttendiesVisible;
    }

}
