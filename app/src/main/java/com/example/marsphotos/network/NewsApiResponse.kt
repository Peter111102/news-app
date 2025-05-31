package com.example.marsphotos.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json


@Serializable
data class NewsApiResponse(
    val status: String = "",
    val totalResults: Int = 0,
    val results: List<NewsArticle> = emptyList(),

    @SerialName("nextPage")
    val nextPage: String? = null
)

@Serializable
data class NewsArticle(
    @SerialName(value = "article_id")
    val articleId: String? = null,

    @SerialName(value = "title")
    val title: String? = null,

    @SerialName(value = "link")
    val link: String? = null,

    @SerialName(value = "keywords")
    val keywords: List<String>? = null,

    @SerialName(value = "creator")
    val creator: List<String>? = null,

    @SerialName(value = "video_url")
    val videoUrl: String? = null,

    @SerialName(value = "description")
    val description: String? = null,

    @SerialName(value = "content")
    val content: String? = null,

    @SerialName(value = "pubDate")
    val pubDate: String? = null,

    @SerialName(value = "image_url")
    val imageUrl: String? = null,

    @SerialName(value = "source_id")
    val sourceId: String? = null,

    @SerialName(value = "source_url")
    val sourceUrl: String? = null,

    @SerialName(value = "source_priority")
    val sourcePriority: Int? = null,

    @SerialName(value = "country")
    val country: List<String>? = null,

    @SerialName(value = "category")
    val category: List<String>? = null,

    @SerialName(value = "language")
    val language: String? = null,

    @SerialName(value = "ai_tag")
    val aiTag: String? = null,

    @SerialName(value = "sentiment")
    val sentiment: String? = null,

    @SerialName(value = "sentiment_stats")
    val sentimentStats: String? = null,

)

