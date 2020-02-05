
package paul.barthuel.go4lunch.data.model.textsearch;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlacesApiTextResult {

    @SerializedName("candidates")
    @Expose
    private List<Candidate> candidates = null;
    @SerializedName("debug_log")
    @Expose
    private DebugLog debugLog;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public DebugLog getDebugLog() {
        return debugLog;
    }

    public void setDebugLog(DebugLog debugLog) {
        this.debugLog = debugLog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
