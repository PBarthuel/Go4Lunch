package paul.barthuel.go4lunch.ui.workmates;

import java.util.Objects;

public class WorkmatesInfo {

    private String name;
    private String image;
    private String id;
    private String placeId;
    private String restaurantName;

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getName() {
        return name;
    }

    public String getImage() { return image; }

    public String getId() { return id; }

    public String getPlaceId() { return placeId; }

    public WorkmatesInfo(String name,
                         String image,
                         String id,
                         String placeId,
                         String restaurantName) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.placeId = placeId;
        this.restaurantName = restaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesInfo that = (WorkmatesInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(image, that.image)&&
                Objects.equals(id, that.id)&&
                Objects.equals(placeId, that.placeId)&&
                Objects.equals(restaurantName, that.restaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image, id, placeId, restaurantName);
    }
}
