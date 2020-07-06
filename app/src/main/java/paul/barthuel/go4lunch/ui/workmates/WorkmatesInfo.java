package paul.barthuel.go4lunch.ui.workmates;

import java.util.Objects;

public class WorkmatesInfo {

    private String name;
    private String image;

    public String getName() {
        return name;
    }

    public String getImage() { return image; }

    public WorkmatesInfo(String name,
                         String image) {
        this.name = name;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesInfo that = (WorkmatesInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image);
    }
}
