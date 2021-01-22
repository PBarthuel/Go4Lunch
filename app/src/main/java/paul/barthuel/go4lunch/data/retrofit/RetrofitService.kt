package paul.barthuel.go4lunch.data.retrofit

import io.reactivex.rxjava3.internal.schedulers.RxThreadFactory
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
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
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        googlePlacesAPI = retrofit.create(GooglePlacesAPI::class.java)
    }
}