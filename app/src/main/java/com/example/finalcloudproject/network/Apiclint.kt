package com.example.finalcloudproject.network
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
object Apiclint {
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create()).build()
            }
            return retrofit
        }
}