package paul.barthuel.go4lunch.ui.workmates;

public class WorkmatesInfo {

    private final String name;
    private final String image;
    private final String id;
    private final String placeId;
    private final String restaurantName;

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


    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkmatesInfo that = (WorkmatesInfo) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (placeId != null ? !placeId.equals(that.placeId) : that.placeId != null) return false;
        return restaurantName != null ? restaurantName.equals(that.restaurantName) : that.restaurantName == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (placeId != null ? placeId.hashCode() : 0);
        result = 31 * result + (restaurantName != null ? restaurantName.hashCode() : 0);
        return result;
    }
}
