package paul.barthuel.go4lunch.data.firestore.user.dto;

public class TodayUser {

    private String userId;
    private String placeId;
    private String restaurantName;

    public TodayUser() { }

    public TodayUser(String userId, String placeId, String restaurantName) {
        this.userId = userId;
        this.placeId = placeId;
        this.restaurantName = restaurantName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlaceId() {
        return placeId;
    }




}
