package paul.barthuel.go4lunch.ui.map_view

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import paul.barthuel.go4lunch.data.local.ActualLocationRepository
import paul.barthuel.go4lunch.data.local.UserSearchRepository
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository
import java.util.*

class LocalisationViewModel(private val actualLocationRepository: ActualLocationRepository,
                            nearbyRepository: NearbyRepository,
                            userSearchRepository: UserSearchRepository) : ViewModel() {
    private val liveDataLunchMarker = MediatorLiveData<List<LunchMarker>>()
    //private val liveDataNearby: LiveData<NearbyResponse>
    private lateinit var entityList: List<LunchMarker>
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    var resultListObservable: PublishSubject<List<LunchMarker>> = PublishSubject.create()

    val uiModelsLiveData: LiveData<List<LunchMarker>>
        get() = liveDataLunchMarker

    private var isMapReady = false
    private var hasLocationPermissions = false

    private fun map(
            nearbyResponse: NearbyResponse?/*,
            userSearchQuery: String?*/): List<LunchMarker> {
        //val lunchMarkers: MutableList<LunchMarker> = ArrayList()
        val lunchMarkers: ArrayList<LunchMarker> = ArrayList()
        if (nearbyResponse?.results != null) {
            for (result in nearbyResponse.results!!) {
                val latitude = result.geometry.location.lat
                val longitude = result.geometry.location.lng
                val name = result.name
                /*var backGroundColor: Int
                backGroundColor = if (userSearchQuery != null && userSearchQuery.isNotEmpty() && name.startsWith(userSearchQuery)) {
                    BitmapDescriptorFactory.HUE_AZURE.toInt()
                } else {
                    BitmapDescriptorFactory.HUE_RED.toInt()
                }*/
                lunchMarkers.add(LunchMarker(latitude, longitude, name/*, backGroundColor*/))
            }
        }
        return lunchMarkers
        //liveDataLunchMarker.value = lunchMarkers
    }

    private fun enableGps() {
        if (isMapReady && hasLocationPermissions) {
            actualLocationRepository.initLocation()
        }
    }

    fun onMapReady() {
        isMapReady = true
        enableGps()
    }

    fun hasPermissions(hasLocationPermissions: Boolean) {
        this.hasLocationPermissions = hasLocationPermissions
        enableGps()
    }

    init {
        /*val locationLiveData = actualLocationRepository.locationLiveData
        val userSearchQueryLiveData = userSearchRepository.userSearchQueryLiveData
        liveDataNearby = Transformations.switchMap(
                locationLiveData) { location: Location? ->
            location?.let {
                nearbyRepository.getNearbyForLocation(it)
            }
        }
        liveDataLunchMarker.addSource(liveDataNearby) { nearbyResponse: NearbyResponse? -> map(nearbyResponse, userSearchQueryLiveData.value) }
        liveDataLunchMarker.addSource(userSearchQueryLiveData) { userSearchQuery: String? -> map(liveDataNearby.value, userSearchQuery) }*/


        //Given
        val location = Location("")
        location.latitude = 48.85838489
        location.longitude = 2.350088

        val disposable: Disposable = nearbyRepository
                .getNearbyForLoc(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<NearbyResponse>() {
                    override fun onSuccess(t: NearbyResponse?) {
                        entityList = map(t)
                        resultListObservable.onNext(map(t))
                    }

                    override fun onError(e: Throwable?) {
                        resultListObservable.isEmpty
                    }

                })
        compositeDisposable.add(disposable)
    }

}