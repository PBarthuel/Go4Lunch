package paul.barthuel.go4lunch.data.firestore.restaurant.dto;

public class Uid {

    private String uid;
    private String workmateName;

    public Uid() {
    }

    public Uid(String uid, String workmateName) {
        this.uid = uid;
        this.workmateName = workmateName;
    }

    public String getWorkmateName() {
        return workmateName;
    }

    public String getUid() {
        return uid;
    }

}
