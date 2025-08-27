package pl.bartpos24.shopmobile.repositories

import pl.bartpos24.web.api.ProductApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
class ProductRepository(private val productApi: ProductApi) : ShopMobileRepository() {

    fun getProductByBarcode(barcode: String) = flow {
        emit(getProductByBarcodeApi(barcode))
    }

    fun getProductFromOpenFoodFacts(barcode: String) = flow {
        emit(getProductFromOpenFoodFactsApi(barcode))
    }

    private suspend fun getProductByBarcodeApi(barcode: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        productApi.apiProductGetProductByBarcodeGet(barcode)
    }
    private suspend fun getProductFromOpenFoodFactsApi(barcode: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        productApi.apiProductGetProductFromOpenFoodFactsGet(barcode)
    }
}