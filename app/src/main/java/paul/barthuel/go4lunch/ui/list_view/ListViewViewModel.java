package paul.barthuel.go4lunch.ui.list_view;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
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
    private ActualLocationRepository actualLocationRepository;

    private LiveData<NearbyResponse> liveDataNearby;
    private MediatorLiveData<Map<String, Detail>> mediatorRestaurantDetail = new MediatorLiveData<>();
    private List<String> alreadyFetchId = new ArrayList<>();

    private MediatorLiveData<Map<String, Integer>> mediatorRestaurantAttendies = new MediatorLiveData<>();
    private List<String> alreadyFetchIdForAttendies = new ArrayList<>();

    private MediatorLiveData<List<RestaurantInfo>> mediatorRestaurantInfo = new MediatorLiveData<>();

    LiveData<List<RestaurantInfo>> getUiModelsLiveData() {
        return mediatorRestaurantInfo;
    }

    public ListViewViewModel(final ActualLocationRepository actualLocationRepository,
                             final NearbyRepository nearbyRepository,
                             final PlaceDetailRepository placeDetailRepository,
                             final RestaurantRepository restaurantRepository,
                             final UriBuilder uriBuilder) {

        mediatorRestaurantDetail.setValue(new HashMap<String, Detail>());
        mediatorRestaurantAttendies.setValue(new HashMap<String, Integer>());
        this.placeDetailRepository = placeDetailRepository;
        this.actualLocationRepository = actualLocationRepository;
        this.restaurantRepository = restaurantRepository;
        this.uriBuilder = uriBuilder;

        liveDataNearby = Transformations.switchMap(
                actualLocationRepository.getLocationLiveData(),
                new Function<Location, LiveData<NearbyResponse>>() {
                    @Override
                    public LiveData<NearbyResponse> apply(Location location) {
                        return nearbyRepository.getNearbyForLocation(location);
                    }
                });

        mediatorRestaurantInfo.addSource(liveDataNearby, new Observer<NearbyResponse>() {
            @Override
            public void onChanged(NearbyResponse nearbyResponse) {
                combineNearbyAndDetails(nearbyResponse,
                        mediatorRestaurantDetail.getValue(),
                        actualLocationRepository.getLocationLiveData().getValue(),
                        mediatorRestaurantAttendies.getValue());
            }
        });
        mediatorRestaurantInfo.addSource(mediatorRestaurantDetail, new Observer<Map<String, Detail>>() {
            @Override
            public void onChanged(Map<String, Detail> restaurantDetailMap) {
                combineNearbyAndDetails(liveDataNearby.getValue(),
                        restaurantDetailMap,
                        actualLocationRepository.getLocationLiveData().getValue(),
                        mediatorRestaurantAttendies.getValue());
            }
        });
        mediatorRestaurantInfo.addSource(actualLocationRepository.getLocationLiveData(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                combineNearbyAndDetails(liveDataNearby.getValue(),
                        mediatorRestaurantDetail.getValue(),
                        location,
                        mediatorRestaurantAttendies.getValue());
            }
        });
        mediatorRestaurantInfo.addSource(mediatorRestaurantAttendies, new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(Map<String, Integer> restaurantAttendies) {
                combineNearbyAndDetails(liveDataNearby.getValue(),
                        mediatorRestaurantDetail.getValue(),
                        actualLocationRepository.getLocationLiveData().getValue(),
                        restaurantAttendies);
            }
        });
    }

    private void combineNearbyAndDetails(@Nullable NearbyResponse nearbyResponse,
                                         @Nullable Map<String, Detail> restaurantDetailMap,
                                         @Nullable Location location,
                                         @Nullable Map<String, Integer> restaurantAttendies) {

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
                        mediatorRestaurantDetail.addSource(
                                placeDetailRepository.getDetailForRestaurantId(result.getPlaceId()),
                                new Observer<Detail>() {
                                    @Override
                                    public void onChanged(Detail detail) {
                                        Map<String, Detail> map = mediatorRestaurantDetail.getValue();
                                        assert map != null;
                                        map.put(detail.getResultDetail().getPlaceId(), detail);
                                        Log.d("courgette", "combineNearbyAndDetails: fetched restaurant detail" + result.getPlaceId());
                                    }
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
                                new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer attendies) {
                                        Map<String, Integer> map = mediatorRestaurantAttendies.getValue();
                                        assert map != null;
                                        map.put(result.getPlaceId(), attendies);
                                        Log.d("courgette", "combineNearbyAndDetails: fetched restaurant attendies" + result.getPlaceId());
                                    }
                                });
                    }
                }
                restaurantInfos.add(map(location, result, detail, attendies));
            }
        }
        mediatorRestaurantInfo.setValue(restaurantInfos);
    }

    private RestaurantInfo map(@NonNull Location location,
                               @NonNull Result result,
                               @Nullable Detail detail,
                               @Nullable Integer attendies) {

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

                        LocalTime localTime = LocalTime.now();

                        if (localTime.isAfter(formattedOpenTime) && localTime.isBefore(formattedCloseTime)) {
                            openingHours = "open until " + formattedCloseTime.toString();
                        } else {
                            String nonFormattedOpenAtTime = periods.get(i + 1).getOpen().getTime();
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

        Double rating = (result.getRating() * 3) / 5;
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
        if (attendies == null || attendies == 0) {
            isAttendiesVisible = false;
        } else {
            isAttendiesVisible = true;
        }

        return new RestaurantInfo(name,
                address,
                openingHours,
                distance,
                rating,
                uri,
                id,
                attendiesAsAString,
                isAttendiesVisible);
    }

    private int getDayNumber() {
        int dayNumber;
        DayOfWeek day = LocalDate.now().getDayOfWeek();
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