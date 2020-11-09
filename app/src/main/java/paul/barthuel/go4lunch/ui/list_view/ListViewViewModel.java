package paul.barthuel.go4lunch.ui.list_view;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.threeten.bp.Clock;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.Period;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

public class ListViewViewModel extends ViewModel {

    private final PlaceDetailRepository placeDetailRepository;
    private final UriBuilder uriBuilder;
    private final RestaurantRepository restaurantRepository;
    private final Clock clock;

    private final LiveData<NearbyResponse> liveDataNearby;
    private final MediatorLiveData<Map<String, Detail>> mediatorRestaurantDetail = new MediatorLiveData<>();
    private final List<String> alreadyFetchId = new ArrayList<>();

    private final MediatorLiveData<Map<String, Integer>> mediatorRestaurantAttendies = new MediatorLiveData<>();
    private final List<String> alreadyFetchIdForAttendies = new ArrayList<>();

    private final MediatorLiveData<List<RestaurantInfo>> mediatorRestaurantInfo = new MediatorLiveData<>();

    LiveData<List<RestaurantInfo>> getUiModelsLiveData() {
        return mediatorRestaurantInfo;
    }

    public ListViewViewModel(final ActualLocationRepository actualLocationRepository,
                             final NearbyRepository nearbyRepository,
                             final PlaceDetailRepository placeDetailRepository,
                             final RestaurantRepository restaurantRepository,
                             final UriBuilder uriBuilder,
                             final UserSearchRepository userSearchRepository,
                             final Clock clock) {

        mediatorRestaurantDetail.setValue(new HashMap<>());
        mediatorRestaurantAttendies.setValue(new HashMap<>());
        this.placeDetailRepository = placeDetailRepository;
        this.restaurantRepository = restaurantRepository;
        this.uriBuilder = uriBuilder;
        this.clock = clock;

        LiveData<Location> locationLiveData = actualLocationRepository.getLocationLiveData();
        LiveData<String> userSearchQueryLiveData = userSearchRepository.getUserSearchQueryLiveData();

        liveDataNearby = Transformations.switchMap(
                actualLocationRepository.getLocationLiveData(),
                nearbyRepository::getNearbyForLocation);

        mediatorRestaurantInfo.addSource(liveDataNearby, nearbyResponse -> combineNearbyAndDetails(nearbyResponse,
                mediatorRestaurantDetail.getValue(),
                locationLiveData.getValue(),
                mediatorRestaurantAttendies.getValue(),
                userSearchQueryLiveData.getValue()));
        mediatorRestaurantInfo.addSource(mediatorRestaurantDetail, restaurantDetailMap -> combineNearbyAndDetails(liveDataNearby.getValue(),
                restaurantDetailMap,
                actualLocationRepository.getLocationLiveData().getValue(),
                mediatorRestaurantAttendies.getValue(),
                userSearchQueryLiveData.getValue()));
        mediatorRestaurantInfo.addSource(locationLiveData, location -> combineNearbyAndDetails(liveDataNearby.getValue(),
                mediatorRestaurantDetail.getValue(),
                location,
                mediatorRestaurantAttendies.getValue(),
                userSearchQueryLiveData.getValue()));
        mediatorRestaurantInfo.addSource(mediatorRestaurantAttendies, restaurantAttendies -> combineNearbyAndDetails(liveDataNearby.getValue(),
                mediatorRestaurantDetail.getValue(),
                locationLiveData.getValue(),
                restaurantAttendies,
                userSearchQueryLiveData.getValue()));
        mediatorRestaurantInfo.addSource(userSearchQueryLiveData, userSearchQuery -> combineNearbyAndDetails(liveDataNearby.getValue(),
                mediatorRestaurantDetail.getValue(),
                locationLiveData.getValue(),
                mediatorRestaurantAttendies.getValue(),
                userSearchQuery));
    }

    private void combineNearbyAndDetails(@Nullable NearbyResponse nearbyResponse,
                                         @Nullable Map<String, Detail> restaurantDetailMap,
                                         @Nullable Location location,
                                         @Nullable Map<String, Integer> restaurantAttendies,
                                         @Nullable String userSearchQuery) {

        if (nearbyResponse == null || location == null || restaurantDetailMap == null || restaurantAttendies == null) {
            return;
        }

        List<RestaurantInfo> restaurantInfos = new ArrayList<>();

        if (nearbyResponse.getResults() != null) {
            for (Result result : nearbyResponse.getResults()) {
                Detail detail = restaurantDetailMap.get(result.getPlaceId());
                if (detail == null) {
                    if (!alreadyFetchId.contains(result.getPlaceId())) {
                        Log.d("courgette", "combineNearbyAndDetails: fetching restaurant detail" + result.getPlaceId());
                        alreadyFetchId.add(result.getPlaceId());
                        LiveData<Detail> detailLiveData = placeDetailRepository.getDetailForRestaurantId(result.getPlaceId());
                        mediatorRestaurantDetail.addSource(
                                detailLiveData,
                                detail1 -> {
                                    Map<String, Detail> map = mediatorRestaurantDetail.getValue();
                                    assert map != null;
                                    map.put(detail1.getResultDetail().getPlaceId(), detail1);
                                    Log.d("courgette", "combineNearbyAndDetails: fetched restaurant detail" + result.getPlaceId());
                                });

                    }
                }
                Integer attendies = restaurantAttendies.get(result.getPlaceId());
                if (attendies == null) {
                    if (!alreadyFetchIdForAttendies.contains(result.getPlaceId())) {
                        Log.d("courgette", "combineNearbyAndDetails: fetching restaurant attendies" + result.getPlaceId());
                        alreadyFetchIdForAttendies.add(result.getPlaceId());
                        mediatorRestaurantAttendies.addSource(
                                restaurantRepository.getRestaurantAttendies(result.getPlaceId()),
                                attendies1 -> {
                                    Map<String, Integer> map = mediatorRestaurantAttendies.getValue();
                                    assert map != null;
                                    map.put(result.getPlaceId(), attendies1);
                                    Log.d("courgette", "combineNearbyAndDetails: fetched restaurant attendies" + result.getPlaceId());
                                });
                    }
                }
                restaurantInfos.add(map(location, result, detail, attendies, userSearchQuery));
            }
        }
        mediatorRestaurantInfo.setValue(restaurantInfos);
    }

    private RestaurantInfo map(@NonNull Location location,
                               @NonNull Result result,
                               @Nullable Detail detail,
                               @Nullable Integer attendies,
                               @Nullable String userSearchQuery) {

        String name = result.getName();
        String address = result.getVicinity();
        String openingHours = "unknow";
        List<Period> periods;
        Period period;

        if (detail == null) {
            openingHours = null;
        } else {
            int dayNumber = getDayNumber();
            if (detail.getResultDetail().getOpeningHours() == null) {
                openingHours = "unknown";
            } else {
                periods = detail.getResultDetail().getOpeningHours().getPeriods();
                for (int i = 0; i < periods.size(); i++) {
                    period = periods.get(i);

                    if (period.getOpen().getDay() == dayNumber) {

                        String nonFormattedOpenTime = period.getOpen().getTime();
                        LocalTime formattedOpenTime = LocalTime.parse(nonFormattedOpenTime, DateTimeFormatter.ofPattern("HHmm"));
                        formattedOpenTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

                        String nonFormattedCloseTime = period.getClose().getTime();
                        LocalTime formattedCloseTime = LocalTime.parse(nonFormattedCloseTime, DateTimeFormatter.ofPattern("HHmm"));
                        formattedCloseTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

                        LocalTime localTime = LocalTime.now(clock);

                        if (localTime.isAfter(formattedOpenTime) && localTime.isBefore(formattedCloseTime)) {
                            openingHours = "open until " + formattedCloseTime.toString();
                        } else {
                            String nonFormattedOpenAtTime = periods.get(i).getOpen().getTime();
                            LocalTime formattedOpenAtTime = LocalTime.parse(nonFormattedOpenAtTime, DateTimeFormatter.ofPattern("HHmm"));
                            formattedOpenAtTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
                            openingHours = "open at " + formattedOpenAtTime.toString();
                        }
                    }

                }
            }
        }

        String distance = "" + distance(result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng(),
                location.getLatitude(),
                location.getLongitude()) + "m";

        double rating = (result.getRating() * 3) / 5;
        if (rating >= 2.50) {
            rating = 3.00;
        } else if (rating < 2.50 && rating >= 1.50) {
            rating = 2.00;
        } else {
            rating = 1.00;
        }


        String uri = null;

        if (result.getPhotos() != null && result.getPhotos().size() > 0) {
            String photoReference = result.getPhotos().get(0).getPhotoReference();
            uri = uriBuilder.buildUri("https",
                    "maps.googleapis.com",
                    "maps/api/place/photo",
                    new Pair<>("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs"),
                    new Pair<>("photoreference", photoReference),
                    new Pair<>("maxwidth", "1080"));
        }

        String id = result.getPlaceId();

        String attendiesAsAString = attendies == null ? "?" : String.valueOf(attendies);

        boolean isAttendiesVisible;
        isAttendiesVisible = attendies != null && attendies != 0;

        int backGroundColor;

        if (userSearchQuery != null && !userSearchQuery.isEmpty() && name.startsWith(userSearchQuery)) {
            backGroundColor = R.color.selected_background_color;
        }else {
            backGroundColor = android.R.color.white;
        }

        return new RestaurantInfo(name,
                address,
                openingHours,
                distance,
                rating,
                uri,
                id,
                attendiesAsAString,
                isAttendiesVisible,
                backGroundColor);
    }

    private int getDayNumber() {
        int dayNumber;
        DayOfWeek day = LocalDate.now(clock).getDayOfWeek();
        switch (day) {
            case SUNDAY:
                dayNumber = 0;
                break;
            case MONDAY:
                dayNumber = 1;
                break;
            case TUESDAY:
                dayNumber = 2;
                break;
            case WEDNESDAY:
                dayNumber = 3;
                break;
            case THURSDAY:
                dayNumber = 4;
                break;
            case FRIDAY:
                dayNumber = 5;
                break;
            case SATURDAY:
                dayNumber = 6;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + day);
        }
        return dayNumber;
    }

    private int distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344 * 1000;
            return (int) dist;
        }
    }
}