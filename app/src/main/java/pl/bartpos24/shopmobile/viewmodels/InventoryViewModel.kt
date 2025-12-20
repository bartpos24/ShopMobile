package pl.bartpos24.shopmobile.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.singleOrNull
import pl.bartpos24.shopmobile.models.BarcodeResult
import pl.bartpos24.shopmobile.repositories.ProductRepository
import pl.bartpos24.shopmobile.scanner.Scanner
import pl.bartpos24.web.model.Product
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import org.threeten.bp.LocalDateTime
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.repositories.InventoryRepository
import pl.bartpos24.shopmobile.utilities.toShopApiMessage
import pl.bartpos24.web.model.Inventory
import pl.bartpos24.web.model.InventoryPosition
import javax.inject.Inject

class InventoryViewModel  @Inject constructor(private val productRepository: ProductRepository, private val inventoryRepository: InventoryRepository, val scanner: Scanner) : ShopMobileViewModel() {
    private val _barcodeResults = MutableStateFlow<BarcodeResult?>(null)
    val barcodeResults: StateFlow<BarcodeResult?> = _barcodeResults

    private val _inventory = MutableStateFlow<Inventory?>(null)
    val inventory: StateFlow<Inventory?> = _inventory

    private val _inventories = MutableStateFlow<List<Inventory>>(emptyList())
    val inventories: StateFlow<List<Inventory>> = _inventories

    private val _inventoryPositions = MutableStateFlow<List<InventoryPosition>>(emptyList())
    val inventoryPositions: StateFlow<List<InventoryPosition>> = _inventoryPositions

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _refresh = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _refresh

    private val _quantity = MutableStateFlow<Double?>(null)
    val quantity: StateFlow<Double?> = _quantity
    private val _price = MutableStateFlow<Double?>(null)
    val price: StateFlow<Double?> = _price
    fun clearBarcodeData() {
        scanner.clearBarcodeResult()
    }

    fun setInventory(inventory: Inventory?) {
        _inventory.value = inventory
    }

    fun refreshInventory() {
        _refresh.value = true
    }

    fun setQuantity(quantity: Double?) {
        _quantity.value = quantity
    }
    fun setPrice(price: Double?) {
        _price.value = price
    }
    fun clearData() {
        _price.value = 0.0
        _quantity.value = 1.0
        _product.value = null
    }

    val barcodeResult = scanner.scannerDataFlow

    init {
        _refresh.filter { it }
            .map {
                inventoryRepository.getAllInventory()
                    .onEach { _inventories.value = it }
                    .onCompletion { _refresh.value = false }
                    .catch { offerError(R.string.toast_err_inventories_404) }
                    .singleOrNull()
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        _inventory
            .filterNotNull()
            .map { getAllInventoryPositionsForUser() }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    suspend fun getProductByBarcode(barcode: String) = productRepository.getProductByBarcode(barcode)
        .map { if(!it.isNullOrEmpty() && it.count() == 1) it.firstOrNull() else null }
        .onEach { _product.value = it }
        .catch {  }
        .singleOrNull()

    suspend fun getProductFromOpenFoodFacts(barcode: String) = productRepository.getProductFromOpenFoodFacts(barcode)
        .catch {
            offerError(it.toShopApiMessage())
        }
        .singleOrNull()

    suspend fun addInventoryPosition() = inventoryRepository.addInventoryPosition(InventoryPosition(id = 0, quantity = _quantity.value, price = _price.value, scanDate = LocalDateTime.now(), productId = _product.value?.id, userId = 0, inventoryId = _inventory.value?.id, product = _product.value))
        .onEach { _inventoryPositions.value = _inventoryPositions.value + it }
        .catch { offerError(it.toShopApiMessage()) }
        .singleOrNull()

    suspend fun getAllInventoryPositionsForUser() = inventoryRepository.getAllInventoryPositionsForUser(_inventory.value?.id ?: 0)
        .onEach { _inventoryPositions.value = it }
        .catch { }
        .singleOrNull()
}