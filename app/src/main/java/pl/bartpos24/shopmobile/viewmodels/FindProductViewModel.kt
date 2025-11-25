package pl.bartpos24.shopmobile.viewmodels

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import pl.bartpos24.shopmobile.models.BarcodeResult
import pl.bartpos24.shopmobile.repositories.ProductRepository
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.scanner.Scanner
import pl.bartpos24.shopmobile.utilities.toShopApiMessage
import pl.bartpos24.web.model.Product
import javax.inject.Inject

class FindProductViewModel @Inject constructor(private val productRepository: ProductRepository, val scanner: Scanner) : ShopMobileViewModel() {

    private val _barcodeResults = MutableStateFlow<BarcodeResult?>(null)
    val barcodeResults: StateFlow<BarcodeResult?> = _barcodeResults

    fun clearBarcodeData() {
        scanner.clearBarcodeResult()
    }

    val barcodeResult = scanner.scannerDataFlow

    fun scannerData() = scanner.scannerFlow()

    suspend fun getProductByBarcode(barcode: String) = productRepository.getProductByBarcode(barcode)
        .catch {
           // offerError(it.toShopApiMessage())
        }
        .singleOrNull()

    suspend fun getProductFromOpenFoodFacts(barcode: String) = productRepository.getProductFromOpenFoodFacts(barcode)
        .catch {
            offerError(it.toShopApiMessage())
        }
        .map { listOf(it) }
        .singleOrNull()
}