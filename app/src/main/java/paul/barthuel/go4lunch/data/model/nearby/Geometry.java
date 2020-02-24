
package paul.barthuel.go4lunch.data.model.nearby;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private NearbyLocation location;

    public NearbyLocation getLocation() {
        return location;
    }

    public void setLocation(NearbyLocation location) {
        this.location = location;
    }

}
