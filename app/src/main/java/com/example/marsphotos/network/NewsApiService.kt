package com.example.marsphotos.network
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.screens.MarsUiState
import retrofit2.http.GET
import com.example.marsphotos.ui.screens.MarsViewModel
import retrofit2.http.Query

private const val API_KEY =  "pub_35897d5b0009ab6deb6b7048d1bd384a91c19"



interface NewsApiService {
    @GET("news")
    suspend fun getPhotos(@Query("apikey") apiKey: String = API_KEY,
                          @Query("country") country: String? = null,
                         @Query("page") page: String? = null,
                         @Query("domain") domain: String? = null ): NewsApiResponse

}

