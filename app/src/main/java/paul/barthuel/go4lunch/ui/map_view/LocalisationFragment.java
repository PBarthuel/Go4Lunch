package paul.barthuel.go4lunch.ui.map_view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import paul.barthuel.go4lunch.injections.ViewModelFactory;

public class LocalisationFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final int REQUEST_FINE_LOCATION = 0;
    private LocalisationViewModel mViewModel;
    private GoogleMap googleMap;

    public static LocalisationFragment newInstance() {

        Bundle args = new Bundle();

        LocalisationFragment fragment = new LocalisationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(LocalisationViewModel.class);
        mViewModel.getUiModelsLiveData().observe(this, lunchMarkers -> {
            for (LunchMarker lunchMarker : lunchMarkers) {
                googleMap.addMarker(
                        new MarkerOptions().position(
                                new LatLng(
                                        lunchMarker.getLatitude(),
                                        lunchMarker.getLongitude()
                                )
                        ).icon(BitmapDescriptorFactory.defaultMarker(lunchMarker.getBackGroundColor()))
                                .title(lunchMarker.getName())
                );
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.hasPermissions(checkPermissions());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        mViewModel.onMapReady();
        boolean hasLocationPermissions = checkPermissions();
        if (hasLocationPermissions) {
            map.setMyLocationEnabled(true);
        }
        mViewModel.hasPermissions(hasLocationPermissions);
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