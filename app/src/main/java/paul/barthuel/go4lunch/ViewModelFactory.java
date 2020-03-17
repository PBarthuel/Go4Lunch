package paul.barthuel.go4lunch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import paul.barthuel.go4lunch.data.retrofit.NearbyAPIRepository;
import paul.barthuel.go4lunch.ui.map_view.LocalisationViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    @NonNull
    private final ActualLocationRepository actualLocationRepository;

    private ViewModelFactory(
            @NonNull ActualLocationRepository actualLocationRepository
    ) {
        this.actualLocationRepository = actualLocationRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            new ActualLocationRepository()
                    );
                }
            }
        }

        return sFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocalisationViewModel.class)) {
            return (T) new LocalisationViewModel(
                    actualLocationRepository,
                    new NearbyAPIRepository() //TODO singleton + injection factory
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
