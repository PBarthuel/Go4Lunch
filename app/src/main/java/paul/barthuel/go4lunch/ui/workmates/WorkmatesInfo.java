package paul.barthuel.go4lunch.ui.workmates;

import java.util.Objects;

public class WorkmatesInfo {

    private String name;
    private String image;
    private String id;

    public String getName() {
        return name;
    }

    public String getImage() { return image; }

    public String getId() { return id; }

    public WorkmatesInfo(String name,
                         String image,
                         String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesInfo that = (WorkmatesInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(image, that.image)&&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image, id);
    }
}
