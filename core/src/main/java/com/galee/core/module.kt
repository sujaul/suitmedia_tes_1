package com.galee.core

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.galee.core.helper.remote.ConnectivityInterceptorImpl
import com.galee.core.helper.remote.NoConnectivityException
import com.galee.core.util.NetworkUtil
import com.google.android.play.core.splitcompat.SplitCompat
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val REQUEST_TIMEOUT = 60

val coreModule = module {
    factory { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }


    factory<OkHttpClient> {
        val sharedPreferences =
            androidContext().getSharedPreferences("setting_api.conf", Context.MODE_PRIVATE)
        val hostname = sharedPreferences.getString("api", null) ?: Constant.BASE_URL
        val profil =
            androidContext().getSharedPreferences("profil.conf", Context.MODE_PRIVATE)
        val token = profil.getString("access_token", null)
        OkHttpClient().newBuilder().apply {
            addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                    if (token != null) {
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                    }

                    val request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
            if (BuildConfig.DEBUG) {
                addInterceptor(get<HttpLoggingInterceptor>())
            }
            connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            addInterceptor(ConnectivityInterceptorImpl(get()))
            addNetworkInterceptor(StethoInterceptor())
            certificatePinner(CertificatePinner.Builder()
                    .add(hostname, "sha1/mBN/TTGneHe2Hq0yFG+SRt5nMZQ=")
                    .add(hostname, "sha1/6CgvsAgBlX3PYiYRGedC0NZw7ys=")
                    .build())
            apply {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun  checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
                })
                sslSocketFactory(SSLContext.getInstance("SSL").apply { init(null, trustAllCerts,
                    SecureRandom()) }.getSocketFactory(), trustAllCerts.get(0) as X509TrustManager)
            }
            hostnameVerifier { _, _ -> true }
        }.build()
    }

    factory<Retrofit> {
        val sharedPreferences =
            androidContext().getSharedPreferences("setting_api.conf", Context.MODE_PRIVATE)
        Retrofit.Builder()
            .client(get())
            .baseUrl(
               sharedPreferences.getString("api", null) ?: Constant.BASE_URL
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        val okHttpClient = OkHttpClient().newBuilder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        //AndroidNetworking.initialize(get(), okHttpClient)
    }
}

class Core(private val context: Context) {
    fun initApp(isDebug:Boolean){
        if (isDebug) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format(
                        "Class:%s: Line: %s, Method: %s",
                        super.createStackElementTag(element),
                        element.lineNumber,
                        element.methodName
                    )
                }
            })
        } else {
            Timber.plant(ReleaseTree())
        }
        Stetho.initializeWithDefaults(context)
        SplitCompat.install(context)
    }
}

open class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        // log your crash to your favourite
        // Sending crash report to Firebase CrashAnalytics

        // FirebaseCrash.report(message);
        // FirebaseCrash.report(new Exception(message));
    }

}
