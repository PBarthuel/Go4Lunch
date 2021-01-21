package paul.barthuel.go4lunch.ui.list_view

import android.location.Location
import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.threeten.bp.Clock
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository
import paul.barthuel.go4lunch.data.local.ActualLocationRepository
import paul.barthuel.go4lunch.data.local.UserSearchRepository
import paul.barthuel.go4lunch.data.model.detail.Detail
import paul.barthuel.go4lunch.data.model.detail.Period
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse
import paul.barthuel.go4lunch.data.model.nearby.Result
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class ListViewViewModel(actualLocationRepository: ActualLocationRepository,
                        nearbyRepository: NearbyRepository,
                        placeDetailRepository: PlaceDetailRepository,
                        restaurantRepository: RestaurantRepository,
                        uriBuilder: UriBuilder,
                        userSearchRepository: UserSearchRepository,
                        clock: Clock) : ViewModel() {
    private val placeDetailRepository: PlaceDetailRepository
    private val uriBuilder: UriBuilder
    private val restaurantRepository: RestaurantRepository
    private val clock: Clock
    private val liveDataNearby: LiveData<NearbyResponse>
    private val mediatorRestaurantDetail = MediatorLiveData<MutableMap<String, Detail>>()
    private val alreadyFetchId: MutableList<String> = ArrayList()
    private val mediatorRestaurantAttendies = MediatorLiveData<MutableMap<String, Int>>()
    private val alreadyFetchIdForAttendies: MutableList<String> = ArrayList()
    private val mediatorRestaurantInfo = MediatorLiveData<List<RestaurantInfo>>()
    val uiModelsLiveData: LiveData<List<RestaurantInfo>>
        get() = mediatorRestaurantInfo

    private fun combineNearbyAndDetails(nearbyResponse: NearbyResponse?,
                                        restaurantDetailMap: Map<String, Detail>?,
                                        location: Location?,
                                        restaurantAttendies: Map<String, Int>?,
                                        userSearchQuery: String?) {
        if (nearbyResponse == null || location == null || restaurantDetailMap == null || restaurantAttendies == null) {
            return
        }
        val restaurantInfos: MutableList<RestaurantInfo> = ArrayList()
        if (nearbyResponse.results != null) {
            for (result in nearbyResponse.results) {
                val detail = restaurantDetailMap[result.placeId]
                if (detail == null) {
                    if (!alreadyFetchId.contains(result.placeId)) {
                        Log.d("courgette", "combineNearbyAndDetails: fetching restaurant detail" + result.placeId)
                        alreadyFetchId.add(result.placeId)
                        val detailLiveData = placeDetailRepository.getDetailForRestaurantId(result.placeId)
                        mediatorRestaurantDetail.addSource(
                                detailLiveData
                        ) { detail1: Detail ->
                            val map = mediatorRestaurantDetail.value!!
                            map[detail1.resultDetail.placeId] = detail1
                            Log.d("courgette", "combineNearbyAndDetails: fetched restaurant detail" + result.placeId)
                        }
                    }
                }
                val attendies = restaurantAttendies[result.placeId]
                if (attendies == null) {
                    if (!alreadyFetchIdForAttendies.contains(result.placeId)) {
                        Log.d("courgette", "combineNearbyAndDetails: fetching restaurant attendies" + result.placeId)
                        alreadyFetchIdForAttendies.add(result.placeId)
                        mediatorRestaurantAttendies.addSource(
                                restaurantRepository.getRestaurantAttendies(result.placeId)
                        ) { attendies1: Int ->
                            val map = mediatorRestaurantAttendies.value!!
                            map[result.placeId] = attendies1
                            Log.d("courgette", "combineNearbyAndDetails: fetched restaurant attendies" + result.placeId)
                        }
                    }
                }
                restaurantInfos.add(map(location, result, detail, attendies, userSearchQuery))
            }
        }
        mediatorRestaurantInfo.value = restaurantInfos
    }

    private fun map(location: Location,
                    result: Result,
                    detail: Detail?,
                    attendies: Int?,
                    userSearchQuery: String?): RestaurantInfo {
        val name = result.name
        val address = result.vicinity
        var openingHours: String = "unknow"
        val periods: List<Period>
        var period: Period
        if (detail == null) {
            openingHours = null.toString()
        } else {
            val dayNumber = dayNumber
            if (detail.resultDetail.openingHours == null) {
                openingHours = "unknown"
            } else {
                periods = detail.resultDetail.openingHours.periods
                for (i in periods.indices) {
                    period = periods[i]
                    if (period.open.day == dayNumber) {
                        val nonFormattedOpenTime = period.open.time
                        val formattedOpenTime = LocalTime.parse(nonFormattedOpenTime, DateTimeFormatter.ofPattern("HHmm"))
                        formattedOpenTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
                        val nonFormattedCloseTime = period.close.time
                        val formattedCloseTime = LocalTime.parse(nonFormattedCloseTime, DateTimeFormatter.ofPattern("HHmm"))
                        formattedCloseTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
                        val localTime = LocalTime.now(clock)
                        openingHours = if (localTime.isAfter(formattedOpenTime) && localTime.isBefore(formattedCloseTime)) {
                            "open until $formattedCloseTime"
                        } else {
                            val nonFormattedOpenAtTime = periods[i].open.time
                            val formattedOpenAtTime = LocalTime.parse(nonFormattedOpenAtTime, DateTimeFormatter.ofPattern("HHmm"))
                            formattedOpenAtTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
                            "open at $formattedOpenAtTime"
                        }
                    }
                }
            }
        }
        val distance = "" + distance(result.geometry.location.lat,
                result.geometry.location.lng,
                location.latitude,
                location.longitude) + "m"
        var rating = result.rating * 3 / 5
        rating = if (rating >= 2.50) {
            3.00
        } else if (rating < 2.50 && rating >= 1.50) {
            2.00
        } else {
            1.00
        }
        var uri: String = null.toString()
        if (result.photos != null && result.photos.size > 0) {
            val photoReference = result.photos[0].photoReference
            uri = uriBuilder.buildUri("https",
                    "maps.googleapis.com",
                    "maps/api/place/photo",
                    Pair("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs"),
                    Pair("photoreference", photoReference),
                    Pair("maxwidth", "1080"))
        }
        val id = result.placeId
        val attendiesAsAString = attendies?.toString() ?: "?"
        val isAttendiesVisible: Boolean = attendies != null && attendies != 0
        val backGroundColor: Int
        backGroundColor = if (userSearchQuery != null && userSearchQuery.isNotEmpty() && name.startsWith(userSearchQuery)) {
            R.color.selected_background_color
        } else {
            android.R.color.white
        }
        return RestaurantInfo(name,
                address,
                openingHours,
                distance,
                rating,
                uri,
                id,
                attendiesAsAString,
                isAttendiesVisible,
                backGroundColor)
    }

    private val dayNumber: Int
        get() {
            val dayNumber: Int
            val day = LocalDate.now(clock).dayOfWeek
            dayNumber = when (day) {
                DayOfWeek.SUNDAY -> 0
                DayOfWeek.MONDAY -> 1
                DayOfWeek.TUESDAY -> 2
                DayOfWeek.WEDNESDAY -> 3
                DayOfWeek.THURSDAY -> 4
                DayOfWeek.FRIDAY -> 5
                DayOfWeek.SATURDAY -> 6
                else -> throw IllegalStateException("Unexpected value: $day")
            }
            return dayNumber
        }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        return when {
            lat1 == lat2 && lon1 == lon2 -> {
                0
            }
            else -> {
                val theta = lon1 - lon2
                var dist = sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(theta))
                dist = acos(dist)
                dist = Math.toDegrees(dist)
                dist *= 60 * 1.1515 * 1.609344 * 1000
                dist.toInt()
            }
        }
    }

    init {
        mediatorRestaurantDetail.value = HashMap()
        mediatorRestaurantAttendies.value = HashMap()
        this.placeDetailRepository = placeDetailRepository
        this.restaurantRepository = restaurantRepository
        this.uriBuilder = uriBuilder
        this.clock = clock
        val locationLiveData = actualLocationRepository.locationLiveData
        val userSearchQueryLiveData = userSearchRepository.userSearchQueryLiveData
        liveDataNearby = Transformations.switchMap(
                actualLocationRepository.locationLiveData) { location: Location? -> nearbyRepository.getNearbyForLocation(location) }
        mediatorRestaurantInfo.addSource(liveDataNearby) { nearbyResponse: NearbyResponse? ->
            combineNearbyAndDetails(nearbyResponse,
                    mediatorRestaurantDetail.value,
                    locationLiveData.value,
                    mediatorRestaurantAttendies.value,
                    userSearchQueryLiveData.value)
        }
        mediatorRestaurantInfo.addSource(mediatorRestaurantDetail) { restaurantDetailMap: Map<String, Detail>? ->
            combineNearbyAndDetails(liveDataNearby.value,
                    restaurantDetailMap,
                    actualLocationRepository.locationLiveData.value,
                    mediatorRestaurantAttendies.value,
                    userSearchQueryLiveData.value)
        }
        mediatorRestaurantInfo.addSource(locationLiveData) { location: Location? ->
            combineNearbyAndDetails(liveDataNearby.value,
                    mediatorRestaurantDetail.value,
                    location,
                    mediatorRestaurantAttendies.value,
                    userSearchQueryLiveData.value)
        }
        mediatorRestaurantInfo.addSource(mediatorRestaurantAttendies) { restaurantAttendies: Map<String, Int>? ->
            combineNearbyAndDetails(liveDataNearby.value,
                    mediatorRestaurantDetail.value,
                    locationLiveData.value,
                    restaurantAttendies,
                    userSearchQueryLiveData.value)
        }
        mediatorRestaurantInfo.addSource(userSearchQueryLiveData) { userSearchQuery: String? ->
            combineNearbyAndDetails(liveDataNearby.value,
                    mediatorRestaurantDetail.value,
                    locationLiveData.value,
                    mediatorRestaurantAttendies.value,
                    userSearchQuery)
        }
    }
}