package pl.bartpos24.shopmobile.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.singleOrNull
import pl.bartpos24.shopmobile.models.BarcodeResult
import pl.bartpos24.shopmobile.repositories.ProductRepository
import pl.bartpos24.shopmobile.scanner.Scanner
import pl.bartpos24.web.model.Product
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InventoryViewModel  @Inject constructor(private val productRepository: ProductRepository, val scanner: Scanner) : ShopMobileViewModel() {
    private val _barcodeResults = MutableStateFlow<BarcodeResult?>(null)
    val barcodeResults: StateFlow<BarcodeResult?> = _barcodeResults

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product
    fun clearBarcodeData() {
        scanner.clearBarcodeResult()
    }

    val barcodeResult = scanner.scannerDataFlow

    suspend fun getProductByBarcode(barcode: String) = productRepository.getProductByBarcode(barcode)
        .map { if(!it.isNullOrEmpty() && it.count() == 1) it.firstOrNull() else null }
        .onEach { _product.value = it }
        .catch {
            offerError(it.message.toString())
        }
        .singleOrNull()

    suspend fun getProductFromOpenFoodFacts(barcode: String) = productRepository.getProductFromOpenFoodFacts(barcode)
        .catch {
            offerError(it.message.toString())
        }
        .singleOrNull()
}