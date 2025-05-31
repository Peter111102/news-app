package com.example.marsphotos.data

import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val prefDao: PrefDao): ItemsRepository {

    override fun getAllItemsStream(): Flow<List<Pref>> =  prefDao.getAllItems()

    override fun getItemStream(id: Int): Flow<Pref?> = prefDao.getItem(id)

    override suspend fun insertItem(item: Pref) = prefDao.insert(item)

    override suspend fun deleteItem(item: Pref) = prefDao.delete(item)

    override suspend fun updateItem(item: Pref) = prefDao.update(item)
}