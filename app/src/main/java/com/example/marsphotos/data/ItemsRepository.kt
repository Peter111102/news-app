package com.example.marsphotos.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemsRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllItemsStream(): Flow<List<Pref>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<Pref?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Pref)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Pref)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Pref)
}