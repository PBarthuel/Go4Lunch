
package paul.barthuel.go4lunch.data.model.textsearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private TextSearchLocation textSearchLocation;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public TextSearchLocation getTextSearchLocation() {
        return textSearchLocation;
    }

    public void setTextSearchLocation(TextSearchLocation textSearchLocation) {
        this.textSearchLocation = textSearchLocation;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
