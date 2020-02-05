
package paul.barthuel.go4lunch.data.model.textsearch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DebugLog {

    @SerializedName("line")
    @Expose
    private List<Object> line = null;

    public List<Object> getLine() {
        return line;
    }

    public void setLine(List<Object> line) {
        this.line = line;
    }

}
