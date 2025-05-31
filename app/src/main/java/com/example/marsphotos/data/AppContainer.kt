package com.example.marsphotos.data

import android.content.Context
import com.example.marsphotos.network.NewsApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


interface AppContainer {
    val newsRepository: NewsRepository
    val itemsRepository: ItemsRepository
}

class DefaultAppContainer( private val context: Context ): AppContainer {

    private val baseUrl = "https://newsdata.io/api/1/"

    private val json = Json {
        ignoreUnknownKeys = true
    }


    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
           json.asConverterFactory("application/json".toMediaType())
        )
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: NewsApiService by lazy {
            retrofit.create(NewsApiService::class.java)
    }

    override val newsRepository: NewsRepository by lazy {
        NetworkNewsRepository(retrofitService)
    }

    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).prefDao())
    }

}
