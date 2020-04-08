package paul.barthuel.go4lunch.data.retrofit;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static RetrofitService sInstance;
    private GooglePlacesAPI googlePlacesAPI;

    private RetrofitService() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    @NonNull
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();

                        HttpUrl url = request.url().newBuilder()
                                //.addQueryParameter("api-key", "AIzaSyBpAscDhuNu69txHj8f1R5zlyHo4mTiSwg")
                                .build();
                        request = request.newBuilder().url(url).build();
                        return chain.proceed(request);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googlePlacesAPI = retrofit.create(GooglePlacesAPI.class);

    }

    public static RetrofitService getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitService();
        }
        return sInstance;
    }

    public GooglePlacesAPI getGooglePlacesAPI() {
        return googlePlacesAPI;
    }
}
