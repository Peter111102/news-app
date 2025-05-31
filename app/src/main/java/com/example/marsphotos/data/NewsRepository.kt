package com.example.marsphotos.data

import com.example.marsphotos.network.NewsApiService
import com.example.marsphotos.network.NewsApiResponse

interface NewsRepository {
    suspend fun getMarsPhotos(page: String?, domain: String?, country: String?): NewsApiResponse
}

class NetworkNewsRepository(
    private val newsApiService: NewsApiService
): NewsRepository{
    override suspend fun getMarsPhotos(page: String?, domain: String?, country: String?): NewsApiResponse = newsApiService.getPhotos( page = page, domain = domain, country = country )
}

