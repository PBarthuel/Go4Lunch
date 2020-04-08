package paul.barthuel.go4lunch.ui.restaurant_detail;

public class RestaurantDetailInfo {
    private String name;
    private String address;
    private String image;
    private String id;
    private String phoneNumber;
    private String url;

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

    public RestaurantDetailInfo(String name,
                                String address,
                                String image,
                                String id,
                                String phoneNumber,
                                String url) {

        this.name = name;
        this.address = address;
        this.image = image;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.url = url;
    }
}
