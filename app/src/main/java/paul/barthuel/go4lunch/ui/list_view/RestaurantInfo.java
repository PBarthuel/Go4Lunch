package paul.barthuel.go4lunch.ui.list_view;

public class RestaurantInfo {

    private String name;
    private String address;
    private String openingHours;
    private String distance;
    private String rating;
    private String image;
    private String id;

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

    public String getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public RestaurantInfo(String name,
                          String address,
                          String openingHours,
                          String distance,
                          String rating,
                          String image,
                          String id) {

        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.distance = distance;
        this.rating = rating;
        this.image = image;
        this.id = id;
    }


}
