/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.NewsRepository
import kotlinx.coroutines.launch
import java.io.IOException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.NewsApplication
import com.example.marsphotos.network.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import android.annotation.SuppressLint
import androidx.compose.runtime.MovableContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.AppViewModelProvider
import com.example.marsphotos.data.ItemsRepository
import com.example.marsphotos.data.Pref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

sealed interface MarsUiState {
    data class Success(val newsArticle:List <NewsArticle>) : MarsUiState
    object Error : MarsUiState
    object Loading : MarsUiState
}

data class NewsInfo(
    val nextPage: String?=null,
    val newsArticle: List<NewsArticle> = emptyList(),
    val scrollState: LazyListState = LazyListState(),
    val scrollStateQ: LazyListState = LazyListState(),
    val newsArticleQ: List<NewsArticle> = emptyList(),
    val nextPageQ: String?=null,
    val prefListString: String?= null,
    val isFirstScreen: Boolean = true,
    val isPrefScreen: Boolean = false,
    val articleLink: String = "",
)


class MarsViewModel(
    private val newsRepository: NewsRepository) : ViewModel() {

    /** The mutable State that stores the status of the most recent request */
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    // Use MutableStateFlow to observe changes in the UI
    private val _uiState = MutableStateFlow<NewsInfo>(NewsInfo())
    val uiState: StateFlow<NewsInfo> = _uiState.asStateFlow()


    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }
    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading
            marsUiState = try {

                if (uiState.value.isFirstScreen) {
                    val nextPageValue = _uiState.value.nextPage
                    Log.d("MarsViewModel", "Fetching Mars photos with nextPage: $nextPageValue")

                    val response = newsRepository.getMarsPhotos(nextPageValue,null,"it")
                    Log.d("MarsViewModel", "get effettuato")

                    _uiState.update { currentState ->
                        currentState.copy(
                            nextPage = response.nextPage,
                            // Append new data to the existing list
                            newsArticle = currentState.newsArticle + response.results
                        )
                    }
                    Log.d("MarsViewModel", "Success")

                    MarsUiState.Success(_uiState.value.newsArticle)
                } else {
                    val nextPageValue = _uiState.value.nextPageQ
                    val domain = _uiState.value.prefListString
                    Log.d("MarsViewModel", "Fetching Mars photos with nextPage: $nextPageValue")

                    val response = newsRepository.getMarsPhotos(nextPageValue, domain, null)

                    _uiState.update { currentState ->
                        currentState.copy(
                            nextPageQ = response.nextPage,
                            // Append new data to the existing list
                            newsArticleQ = currentState.newsArticleQ + response.results
                        )
                    }
                    MarsUiState.Success(_uiState.value.newsArticleQ)
                }

            } catch (e: IOException) {
                Log.e("MarsViewModel", "Error fetching Mars photos: $e")
                MarsUiState.Error
            } catch (e: HttpException) {
                Log.e("MarsViewModel", "HTTP error fetching Mars photos: $e")
                MarsUiState.Error
            }
        }
    }

    /**
    Because the Android framework does not allow a ViewModel to be passed values in the constructor
    when created, we implement a ViewModelProvider.Factory object, which lets us get around this limitation.
    The Factory pattern is a creational pattern used to create objects. The MarsViewModel.Factory object
    uses the application container to retrieve the marsPhotosRepository, and then passes this repository
    to the ViewModel when the ViewModel object is created.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NewsApplication)
                val newsRepository = application.container.newsRepository
                MarsViewModel(newsRepository = newsRepository)
            }
        }
    }

    fun updatePrefList(prefList: List<Pref>) {
        val newPrefListString = prefList.joinToString(separator = ",") { it.keyword }
        _uiState.update { currentState->
            currentState.copy(
                prefListString = newPrefListString
            )
        }
    }

    fun modifyIsFirstScreen() {

      if(uiState.value.isFirstScreen){
          if(_uiState.value.prefListString == "" || _uiState.value.prefListString == null){

              _uiState.update { currentState->
                  currentState.copy(
                      isPrefScreen = true,
                      isFirstScreen = false,
                  )
              }
          }else{
              _uiState.update { currentState->
                  currentState.copy(
                      isFirstScreen = false,
                      isPrefScreen = false,
                  )
              }
          }
      }else{
          _uiState.update { currentState->
              currentState.copy(
                  isFirstScreen = true
              )
          }
      }
    }

    fun modifyIsPrefScreen() {
        if (_uiState.value.prefListString == "" || _uiState.value.prefListString == null) {

            _uiState.update { currentState ->
                currentState.copy(
                    isPrefScreen = false,
                    isFirstScreen = true,
                )
            }

        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isPrefScreen = !uiState.value.isPrefScreen,
                    isFirstScreen = false
                )
            }
        }
    }

    private fun resetNewsArticleQlist() {
        _uiState.update { currentSate ->
            currentSate.copy(
                newsArticleQ = emptyList()
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeAgo(dateTimeString: String): String {
        if (dateTimeString.isBlank()) {
            return ""
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)

        if (date != null) {
            val calendar = Calendar.getInstance().apply {
                time = date
            }

            val now = Calendar.getInstance()
            val diffInMillis = abs(now.timeInMillis - calendar.timeInMillis)
            val diffInMinutes = diffInMillis / (60 * 1000)

            return when {
                diffInMinutes < 60 -> {
                    "${diffInMinutes}m"
                }
                diffInMinutes < 24 * 60 -> {
                    val diffInHours = diffInMinutes / 60
                    "${diffInHours}h"
                }
                else -> {
                    val diffInDays = diffInMinutes / (24 * 60)
                    "${diffInDays}g"
                }
            }
        }
        return "Invalid date format"
    }

}