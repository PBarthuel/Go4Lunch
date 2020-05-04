package paul.barthuel.go4lunch.data.firestore.restaurant.dto;


public class Restaurant {

    private String placeId;
    private String date;

    public Restaurant() {
    }

    public Restaurant(String placeId, String date) {
        this.placeId = placeId;
        this.date = date;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getDate() {
        return date;
    }

    public void setUid(String placeId) {
        this.placeId = placeId;
    }

    public String setDate() {
        return date;
    }

}
