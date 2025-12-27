package pl.bartpos24.shopmobile.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.singleOrNull
import pl.bartpos24.shopmobile.models.BarcodeResult
import pl.bartpos24.shopmobile.repositories.ProductRepository
import pl.bartpos24.shopmobile.scanner.Scanner
import pl.bartpos24.shopmobile.utilities.toShopApiMessage
import pl.bartpos24.web.model.Product
import pl.bartpos24.web.model.ProductUnit
import javax.inject.Inject

class ProductViewModel @Inject constructor(private val productRepository: ProductRepository, val scanner: Scanner) : ShopMobileViewModel() {

    private val _barcodeResults = MutableStateFlow<BarcodeResult?>(null)
    val barcodeResults: StateFlow<BarcodeResult?> = _barcodeResults

    private val _productBarcode = MutableStateFlow<String?>(null)
    val productBarcode: StateFlow<String?> = _productBarcode

    private val _productBrand = MutableStateFlow<String?>(null)
    val productBrand: StateFlow<String?> = _productBrand

    private val _productName = MutableStateFlow<String?>(null)
    val productName: StateFlow<String?> = _productName

    private val _productCapacity = MutableStateFlow<String?>(null)
    val productCapacity: StateFlow<String?> = _productCapacity

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _units = MutableStateFlow<List<ProductUnit>>(emptyList())
    val units: StateFlow<List<ProductUnit>> = _units

    private val _unit = MutableStateFlow<ProductUnit?>(null)
    val unit: StateFlow<ProductUnit?> = _unit

    init {
        productRepository.getAllUnits()
            .onEach { _units.value = it }
            .catch { offerError(it.toShopApiMessage()) }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
    fun setUnit(unit: ProductUnit?) {
        _unit.value = unit
    }
    fun clearBarcodeData() {
        scanner.clearBarcodeResult()
    }

    val barcodeResult = scanner.scannerDataFlow

    suspend fun getProductByBarcode(barcode: String) = productRepository.getProductByBarcode(barcode)
        .catch {  }
        .singleOrNull()

    suspend fun getProductFromOpenFoodFacts(barcode: String) = productRepository.getProductFromOpenFoodFacts(barcode)
        .catch { offerError(it.toShopApiMessage()) }
        .map { listOf(it) }
        .singleOrNull()

    //suspend fun addEditProduct(product: Product)
}