package paul.barthuel.go4lunch;

import android.location.Location;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import paul.barthuel.go4lunch.data.retrofit.GooglePlacesAPI;
import retrofit2.Call;

public class ActualLocationRepository {

    private static ActualLocationRepository sInstance;
    private Call<GooglePlacesAPI> callGooglePlacesAPI;

    public static ActualLocationRepository getInstance() {

        if (sInstance == null) {
            sInstance = new ActualLocationRepository();
        }

        return sInstance;
    }

    private MutableLiveData<Location> mutableLiveData = new MutableLiveData<>();


    public LiveData<Location> getLocationLiveData() {
        return mutableLiveData;
    }



    public void initLocation() {

        FusedLocationProviderClient mFusedLocation;

        mFusedLocation = LocationServices.getFusedLocationProviderClient(MainApplication.getApplication());

        mFusedLocation.requestLocationUpdates(new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setFastestInterval(5000)
                        .setInterval(10000)
                , new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if(locationResult.getLocations().size() > 0) {
                            mutableLiveData.postValue(locationResult.getLocations().get(0));
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                    }
                }, Looper.getMainLooper());
    }
}
