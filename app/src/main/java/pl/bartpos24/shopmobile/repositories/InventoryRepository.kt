package pl.bartpos24.shopmobile.repositories

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import pl.bartpos24.web.api.InventoryApi
import pl.bartpos24.web.model.CommonInventoryPosition
import pl.bartpos24.web.model.InventoryPosition
import kotlin.coroutines.CoroutineContext

class InventoryRepository(private val inventoryApi: InventoryApi) : ShopMobileRepository() {
    fun getAllInventory() = flow {
        emit(getAllInventoryApi())
    }
    fun getAllInventoryPositionsForUser(inventoryId: Int, userId: Int? = null) = flow {
        emit(getAllInventoryPositionsForUserApi(inventoryId, userId))
    }
    fun getAllCommonInventoryPositionsForUser(inventoryId: Int, userId: Int? = null) = flow {
        emit(getAllCommonInventoryPositionsForUserApi(inventoryId, userId))
    }
    fun addInventoryPosition(inventoryPosition: InventoryPosition) = flow {
        emit(addInventoryPositionApi(inventoryPosition))
    }
    fun addCommonInventoryPosition(commonInventoryPosition: CommonInventoryPosition) = flow {
        emit(addCommonInventoryPositionApi(commonInventoryPosition))
    }
    fun editInventoryPosition(inventoryPosition: InventoryPosition) = flow {
        emit(editInventoryPositionApi(inventoryPosition))
    }
    fun editCommonInventoryPosition(commonInventoryPosition: CommonInventoryPosition) = flow {
        emit(editCommonInventoryPositionApi(commonInventoryPosition))
    }
//    fun deleteInventoryPosition(inventoryPosition: InventoryPosition) = flow {
//        emit(deleteInventoryPositionApi(inventoryPosition))
//    }
//    fun deleteCommonInventoryPosition(commonInventoryPosition: CommonInventoryPosition) = flow {
//        emit(deleteCommonInventoryPositionApi(commonInventoryPosition))
//    }

    private suspend fun getAllInventoryApi(context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryGetAllInventoriesGet()
    }
    private suspend fun getAllInventoryPositionsForUserApi(inventoryId: Int, userId: Int? = null, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryGetAllInventoryPositionsForUserGet(inventoryId, userId)
    }
    private suspend fun getAllCommonInventoryPositionsForUserApi(inventoryId: Int, userId: Int? = null, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryGetAllCommonInventoryPositionsForUserGet(inventoryId, userId)
    }
    private suspend fun addInventoryPositionApi(inventoryPosition: InventoryPosition, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryAddInventoryPositionPost(inventoryPosition)
    }
    private suspend fun addCommonInventoryPositionApi(commonInventoryPosition: CommonInventoryPosition, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryAddCommonInventoryPositionPost(commonInventoryPosition)
    }
    private suspend fun editInventoryPositionApi(inventoryPosition: InventoryPosition, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryEditInventoryPositionPost(inventoryPosition)
    }
    private suspend fun editCommonInventoryPositionApi(commonInventoryPosition: CommonInventoryPosition, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryEditCommonInventoryPositionPost(commonInventoryPosition)
    }
//    private suspend fun deleteInventoryPositionApi(inventoryPosition: InventoryPosition, context: CoroutineContext = coroutineContext) = withContext(context = context) {
//        inventoryApi.apiInventoryDeleteInventoryPositionPost(inventoryPosition)
//    }
}