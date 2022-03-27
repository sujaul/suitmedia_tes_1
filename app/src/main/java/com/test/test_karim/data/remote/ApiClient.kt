package com.test.test_karim.data.remote

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


object ApiClient {

    val REQUEST_TIMEOUT = 1

    fun getClient(context: Context, apiToken: String?, apiURL : String): Retrofit {

        val apiAddress = apiURL

        val okHttpClient = initOkHttp(apiToken, apiAddress)
        val retrofit:Retrofit = Retrofit.Builder()
                .baseUrl(apiAddress)
                .client(okHttpClient)
                // .addCallAdapterFactory(RxJava2CallAdapterFactory.detail())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit
    }

    private fun initOkHttp(apiToken: String?, hostname: String): OkHttpClient {
        val certificatePinner =  CertificatePinner.Builder()
            .add(hostname, "sha1/mBN/TTGneHe2Hq0yFG+SRt5nMZQ=")
            .add(hostname, "sha1/6CgvsAgBlX3PYiYRGedC0NZw7ys=")
            .build()

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun  checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory()

        val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.MINUTES)
                .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.MINUTES)
                .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.MINUTES)
//                .connectionSpecs(Collections.singletonList(spec))
                .cache(null)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(interceptor)
        httpClient.certificatePinner(certificatePinner)
        try {
            httpClient.sslSocketFactory(sslSocketFactory, trustAllCerts.get(0) as X509TrustManager)
            httpClient.hostnameVerifier { _, _ -> true }
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: Exception) {
            throw RuntimeException(e);
        }

        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                if (apiToken != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $apiToken")
                }

                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        })
        val okHttpClient = httpClient.build()
        return okHttpClient
    }

}