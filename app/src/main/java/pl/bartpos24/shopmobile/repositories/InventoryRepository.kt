package pl.bartpos24.shopmobile.repositories

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import pl.bartpos24.web.api.InventoryApi
import pl.bartpos24.web.model.InventoryPosition
import kotlin.coroutines.CoroutineContext

class InventoryRepository(private val inventoryApi: InventoryApi) : ShopMobileRepository() {
    fun getAllInventory() = flow {
        emit(getAllInventoryApi())
    }
    fun getAllInventoryPositionsForUser(inventoryId: Int, userId: Int? = null) = flow {
        emit(getAllInventoryPositionsForUserApi(inventoryId, userId))
    }

    fun addInventoryPosition(inventoryPosition: InventoryPosition) = flow {
        emit(addInventoryPositionApi(inventoryPosition))
    }

    private suspend fun getAllInventoryApi(context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryGetAllInventoriesGet()
    }
    private suspend fun getAllInventoryPositionsForUserApi(inventoryId: Int, userId: Int? = null, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryGetAllInventoryPositionsForUserGet(inventoryId, userId)
    }
    private suspend fun addInventoryPositionApi(inventoryPosition: InventoryPosition, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        inventoryApi.apiInventoryAddInventoryPositionPost(inventoryPosition)
    }
}