package wing.tree.bionda.data.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.qualifier.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val MID_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/"
    private const val RISE_SET_INFO_SERVICE_URL = "http://apis.data.go.kr/B090041/openapi/service/RiseSetInfoService/"
    private const val VILAGE_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val tikXml = TikXml.Builder()
        .exceptionOnUnreadXml(false)
        .build()

    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(httpLoggingInterceptor)
        .build()

    @Provides
    @Qualifier.MidFcstInfoService
    fun providesMidFcstInfoServiceRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(MID_FCST_INFO_SERVICE_URL)
        .client(okHttpClient)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Qualifier.RiseSetInfoService
    fun providesRiseSetInfoServiceRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(RISE_SET_INFO_SERVICE_URL)
        .client(okHttpClient)
        .addConverterFactory(TikXmlConverterFactory.create(tikXml))
        .build()

    @Provides
    @Qualifier.VilageFcstInfoService
    fun providesVilageFcstInfoServiceRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(VILAGE_FCST_INFO_SERVICE_URL)
        .client(okHttpClient)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
}
