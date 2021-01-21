package paul.barthuel.go4lunch.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService private constructor() {
    val googlePlacesAPI: GooglePlacesAPI
    private val retrofit: Retrofit? = null

    companion object {
        private var sInstance: RetrofitService? = null
        val instance: RetrofitService?
            get() {
                if (sInstance == null) {
                    sInstance = RetrofitService()
                }
                return sInstance
            }
    }

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor { chain: Interceptor.Chain ->
                    var request = chain.request()
                    val url = request.url().newBuilder() //.addQueryParameter("api-key", "AIzaSyBpAscDhuNu69txHj8f1R5zlyHo4mTiSwg")
                            .build()
                    request = request.newBuilder().url(url).build()
                    chain.proceed(request)
                }.build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        googlePlacesAPI = retrofit.create(GooglePlacesAPI::class.java)
    }
}