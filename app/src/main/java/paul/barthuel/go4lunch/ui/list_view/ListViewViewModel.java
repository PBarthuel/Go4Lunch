package paul.barthuel.go4lunch.ui.list_view;

import android.location.Location;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
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
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.Period;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

public class ListViewViewModel extends ViewModel {

    private final PlaceDetailRepository placeDetailRepository;
    private ActualLocationRepository actualLocationRepository;

    private LiveData<NearbyResponse> liveDataNearby;
    private MediatorLiveData<Map<String, Detail>> mediatorRestaurantDetail = new MediatorLiveData<>();

    private MediatorLiveData<List<RestaurantInfo>> mediatorRestaurantInfo = new MediatorLiveData<>();

    LiveData<List<RestaurantInfo>> getUiModelsLiveData() {
        return mediatorRestaurantInfo;
    }

    public ListViewViewModel(final ActualLocationRepository repository,
                             final NearbyRepository nearbyRepository,
                             final PlaceDetailRepository placeDetailRepository) {

        mediatorRestaurantDetail.setValue(new HashMap<String, Detail>());
        this.placeDetailRepository = placeDetailRepository;
        this.actualLocationRepository = repository;

        liveDataNearby = Transformations.switchMap(
                repository.getLocationLiveData(),
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
                        repository.getLocationLiveData().getValue());
            }
        });
        mediatorRestaurantInfo.addSource(mediatorRestaurantDetail, new Observer<Map<String, Detail>>() {
            @Override
            public void onChanged(Map<String, Detail> restaurantDetailMap) {
                combineNearbyAndDetails(liveDataNearby.getValue(),
                        restaurantDetailMap,
                        repository.getLocationLiveData().getValue());
            }
        });
        mediatorRestaurantInfo.addSource(repository.getLocationLiveData(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                combineNearbyAndDetails(liveDataNearby.getValue(),
                        mediatorRestaurantDetail.getValue(),
                        location);
            }
        });
    }

    private void combineNearbyAndDetails(@Nullable NearbyResponse nearbyResponse,
                                         @Nullable Map<String, Detail> restaurantDetailMap,
                                         @Nullable Location location) {

        if (nearbyResponse == null || location == null || restaurantDetailMap == null) {
            return;
        }

        List<RestaurantInfo> restaurantInfos = new ArrayList<>();

        for (Result result : nearbyResponse.getResults()) {
            Detail detail = restaurantDetailMap.get(result.getPlaceId());
            if (detail == null) {
                mediatorRestaurantDetail.addSource(
                        placeDetailRepository.getDetailForRestaurantId(result.getPlaceId()),
                        new Observer<Detail>() {
                            @Override
                            public void onChanged(Detail detail) {
                                Map<String, Detail> map = mediatorRestaurantDetail.getValue();
                                assert map != null;
                                map.put(detail.getResultDetail().getPlaceId(), detail);
                            }
                        });

                restaurantInfos.add(map(location, result, null));
            } else {

                restaurantInfos.add(map(location, result, detail));
            }
        }

        mediatorRestaurantInfo.setValue(restaurantInfos);
    }

    private RestaurantInfo map(@NonNull Location location,
                               @NonNull Result result,
                               @Nullable Detail detail) {

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
                //TODO algo pour opening hours finir
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

        String rating = result.getReference();

        String photoReference = result.getPhotos().get(0).getPhotoReference();
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("maps.googleapis.com")
                .path("maps/api/place/photo")
                .appendQueryParameter("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                .appendQueryParameter("photoreference", photoReference)
                .appendQueryParameter("maxwidth", "1080")
                .build();

        String id = result.getPlaceId();

        return new RestaurantInfo(name,
                address,
                openingHours,
                distance,
                rating,
                uri.toString(),
                id);
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