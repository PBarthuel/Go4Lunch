package paul.barthuel.go4lunch.ui.map_view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.ViewModelFactory;

public class LocalisationFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final int REQUEST_FINE_LOCATION = 0;
    private LocalisationViewModel mLocalisationViewModel;

    public static LocalisationFragment newInstance() {

        Bundle args = new Bundle();

        LocalisationFragment fragment = new LocalisationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        if(checkPermissions()) {
            map.setMyLocationEnabled(true);
            ActualLocationRepository.getInstance().initLocation(requireContext());
            mLocalisationViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(LocalisationViewModel.class);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }
}