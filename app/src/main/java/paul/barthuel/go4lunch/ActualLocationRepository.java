package paul.barthuel.go4lunch;

import android.content.Context;
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

public class ActualLocationRepository {

    private static ActualLocationRepository sInstance;

    public static ActualLocationRepository getInstance() {

        if (sInstance == null) {
            sInstance = new ActualLocationRepository();
        }

        return sInstance;
    }

    public LiveData<Location> getLocationLiveData() {
        return mutableLiveData;
    }

    private MutableLiveData<Location> mutableLiveData = new MutableLiveData<>();


    public void initLocation(Context context) {

        FusedLocationProviderClient mFusedLocation;

        mFusedLocation = LocationServices.getFusedLocationProviderClient(context);

        mFusedLocation.requestLocationUpdates(new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setFastestInterval(10000)
                        .setInterval(60000)
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
