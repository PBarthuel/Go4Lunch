package paul.barthuel.go4lunch.data.local

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import paul.barthuel.go4lunch.MainApplication.Companion.applicationContext

class ActualLocationRepository {
    private val mutableLiveData = MutableLiveData<Location>()
    val locationLiveData: LiveData<Location>
        get() = mutableLiveData

    @SuppressLint("MissingPermission")
    fun initLocation() {
        val mFusedLocation: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext())
        mFusedLocation.requestLocationUpdates(LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5000)
                .setInterval(10000)
                , object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.size > 0) {
                    mutableLiveData.postValue(locationResult.locations[0])
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {}
        }, Looper.getMainLooper())
    }

    companion object {
        private var sInstance: ActualLocationRepository? = null
        val instance: ActualLocationRepository?
            get() {
                if (sInstance == null) {
                    sInstance = ActualLocationRepository()
                }
                return sInstance
            }
    }
}