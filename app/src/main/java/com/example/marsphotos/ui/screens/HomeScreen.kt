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

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.marsphotos.R
import com.example.marsphotos.ui.theme.MarsPhotosTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.marsphotos.network.NewsArticle
import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.marsphotos.AppViewModelProvider

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    marsViewModel: MarsViewModel = viewModel(factory = MarsViewModel.Factory)
) {
    val marsUiState = marsViewModel.marsUiState
    val uiState by marsViewModel.uiState.collectAsState()

    val itemsViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by itemsViewModel.homeUiState.collectAsState()

        when (marsUiState) {
            is MarsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
            is MarsUiState.Success -> {

                val topAppBar: @Composable () -> Unit = {
                    FirstScreenTopAppBar(
                        text = stringResource(id = R.string.your_news),
                        onClick = {
                            marsViewModel.updatePrefList(homeUiState.itemList)
                            marsViewModel.modifyIsFirstScreen()
                        }
                    )
                }

                val screenContent: @Composable (List<NewsArticle>, LazyListState, () -> Unit,  topAppBar: @Composable () -> Unit) -> Unit =
                    { news, scrollState, _, topAppBar ->
                        NewsScreen(
                            modifier = modifier.fillMaxWidth(),
                            news,
                            scrollState,
                            { marsViewModel.getMarsPhotos() },
                            topAppBar,
                            marsViewModel::getTimeAgo
                        )
                    }

                if (uiState.isFirstScreen) {
                    screenContent(uiState.newsArticle, uiState.scrollState, { marsViewModel.getMarsPhotos() },topAppBar)
                } else {
                    val topAppBar: @Composable () -> Unit = {
                        SecondScreenTopAppBar(
                            text = stringResource(id = R.string.latest_news),
                            goToFirstScreen = { marsViewModel.modifyIsFirstScreen() },
                            goToPrefScreen = { marsViewModel.modifyIsPrefScreen() }
                        )
                    }
                    screenContent(uiState.newsArticleQ, uiState.scrollStateQ, { marsViewModel.getMarsPhotos() },topAppBar)
                }
            }
            is MarsUiState.Error -> ErrorScreen(
                modifier = modifier.fillMaxSize(),
                onClick = { marsViewModel.getMarsPhotos() },
                topBar = {
                    if (uiState.isFirstScreen) {
                        FirstScreenTopAppBar(
                            text = stringResource(id = R.string.your_news),
                            onClick = {
                                marsViewModel.updatePrefList(homeUiState.itemList)
                                marsViewModel.modifyIsFirstScreen()
                            }
                        )
                    } else {
                        SecondScreenTopAppBar(
                            text = stringResource(id = R.string.latest_news),
                            goToFirstScreen = { marsViewModel.modifyIsFirstScreen() },
                            goToPrefScreen = { marsViewModel.modifyIsPrefScreen() }
                        )
                    }
                }
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreenTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    TopAppBar(
        title = { Text(text = text) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward
                    , contentDescription = stringResource(R.string.latest_news)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreenTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    goToFirstScreen: () -> Unit,
    goToPrefScreen: () -> Unit,
    ){
    TopAppBar(
        title = {
            Text(text = text)
        },
        actions = {
            IconButton(onClick = goToPrefScreen) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(id = R.string.pref_button)
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = goToFirstScreen) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        }
    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    topBar: @Composable () -> Unit
) {
    Scaffold(
        topBar = topBar
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
            )
            Button(onClick = onClick) {
                Text(text = stringResource(R.string.retry), modifier = Modifier.padding(16.dp))
            }
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    articleListToUse: List<NewsArticle>,
    state: LazyListState,
    getMarsPhotos: ()->Unit,
    topBar: @Composable () -> Unit,
    getTimeAgo: (String) -> String,
) {
    Scaffold(
        topBar = topBar
    ){

        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
        ) {
            // Display items
            items(articleListToUse) { article ->
                NewsCard(
                    title = article.title ?: "",
                    image = article.imageUrl ?: "",
                    sourceId = article.sourceId ?: "",
                    data = getTimeAgo(article.pubDate ?: ""),
                    link = article.link ?: "",
                )
            }
            item {
                LaunchedEffect(true) {
                    Log.d("check", "work pls")
                    getMarsPhotos()
                }
            }
        }
    }
}

@Composable
fun NewsCard(
    modifier: Modifier = Modifier,
    title: String,
    image: String,
    sourceId: String,
    data: String,
    link: String,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openUrl(context, url = link) }
            .border(
                border = BorderStroke(Dp.Hairline, SolidColor(Color.LightGray)),
                shape = RectangleShape
            )
    ) {

        Column {
            if(image != ""){
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current).data(image)
                        .crossfade(true).build(),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                    contentDescription = stringResource(R.string.mars_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(420.dp)
                        .height(220.dp)
                        .padding(top = 15.dp, start = 15.dp, end = 15.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            Text(
                text = title,
                modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 10.dp, end = 15.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold // Set the fontWeight to bold
            )

            Text(
                text = "$sourceId - $data",
                modifier = modifier.padding(start = 15.dp, bottom = 10.dp),
                fontSize = 15.sp,
            )
        }
    }
}




fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    MarsPhotosTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    MarsPhotosTheme {
      //  ErrorScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    MarsPhotosTheme {
       // ResultScreen()
    }
}
