package com.example.marsphotos.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.AppViewModelProvider
import com.example.marsphotos.R
import com.example.marsphotos.data.Pref
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreenTopAppBar(
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
                    imageVector = Icons.Filled.ArrowBack
                    , contentDescription = stringResource(R.string.back_button)
                )

            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun PreferenceScreen(){

    val newsViewModel: MarsViewModel = viewModel()
    val uiState by newsViewModel.uiState.collectAsState()

    val itemsViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by itemsViewModel.homeUiState.collectAsState()

    val itemEntryViewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val coroutineScope = rememberCoroutineScope()

        Scaffold (
            topBar = {
                PreferenceScreenTopAppBar(
                    text = stringResource(id = R.string.your_news),
                    onClick = {
                        newsViewModel.updatePrefList(homeUiState.itemList)
                        newsViewModel.modifyIsPrefScreen()} //navigate to your_news screen logic
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 100.dp), // Adjust the padding as needed
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedTextField(
                    value = itemEntryViewModel.itemUiState.itemDetails.keyword,
                    singleLine = true,
                    shape = shapes.large,
                    modifier = Modifier.padding(start = 10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    onValueChange = {
                        itemEntryViewModel.updateUiState(itemEntryViewModel.itemUiState.itemDetails.copy(keyword = it))
                    },
                    label = {
                        Text(text = stringResource(id = R.string.keywords))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            coroutineScope.launch {
                                itemEntryViewModel.saveItem()

                                newsViewModel.updatePrefList(itemsViewModel.homeUiState.value.itemList)
                                itemEntryViewModel.resetKeyword(itemEntryViewModel.itemUiState.itemDetails.copy(keyword = ""))
                            }

                        }
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                ) {
                    items(items = homeUiState.itemList){pref ->
                        ShowPref(pref, {
                            coroutineScope.launch {
                                itemEntryViewModel.deleteItem(pref)
                            }
                            newsViewModel.updatePrefList(itemsViewModel.homeUiState.value.itemList)
                        }
                        )
                    }
                }
                Log.d("pls","work")
            }
        }
}

@Composable
fun ShowPref(
    pref: Pref,
    removePrefFromPrefList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .wrapContentWidth()
            .padding(10.dp),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = removePrefFromPrefList) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.delete_button),
                )
            }
            Text(
                text = pref.keyword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PreferenceErrorScreen(
    goToPreferenceScreen: () -> Unit
){
    Scaffold (
        topBar = {
            PreferenceScreenTopAppBar(
                text = stringResource(id = R.string.your_news),
                onClick = goToPreferenceScreen //navigate to your_news screen logic
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = 100.dp) // Adjust the padding as needed
        ) {
            Button(onClick = goToPreferenceScreen) {
                Text(text = "Nessuna preferenza valida trovata")
            }
        }
    }
}

@Preview
@Composable
fun PreferenceScreenPreview() {
}
