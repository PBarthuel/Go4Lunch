package paul.barthuel.go4lunch.ui.restaurant_detail;

public class RestaurantDetailInfo {
    private final String name;
    private final String address;
    private final String image;
    private final String id;
    private final String phoneNumber;
    private final String url;
    private final Boolean isUserGoing;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getIsUserGoing() { return isUserGoing; }

    public RestaurantDetailInfo(String name,
                                String address,
                                String image,
                                String id,
                                String phoneNumber,
                                String url,
                                Boolean isUserGoing) {

        this.name = name;
        this.address = address;
        this.image = image;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.url = url;
        this.isUserGoing = isUserGoing;
    }
}
