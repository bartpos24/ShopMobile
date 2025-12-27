package pl.bartpos24.shopmobile.repositories

import pl.bartpos24.web.api.ProductApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import pl.bartpos24.web.model.Product
import kotlin.coroutines.CoroutineContext
class ProductRepository(private val productApi: ProductApi) : ShopMobileRepository() {

    fun getProductByBarcode(barcode: String) = flow {
        emit(getProductByBarcodeApi(barcode))
    }
    fun getProductFromOpenFoodFacts(barcode: String) = flow {
        emit(getProductFromOpenFoodFactsApi(barcode))
    }
    fun getAllUnits() = flow {
        emit(getAllUnitsApi())
    }
    fun addEditProduct(product: Product, barcode: String?) = flow {
        emit(addEditProductApi(product, barcode))
    }

    private suspend fun getProductByBarcodeApi(barcode: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        productApi.apiProductGetProductByBarcodeGet(barcode)
    }
    private suspend fun getProductFromOpenFoodFactsApi(barcode: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        productApi.apiProductGetProductFromOpenFoodFactsGet(barcode)
    }
    private suspend fun getAllUnitsApi(context: CoroutineContext = coroutineContext) = withContext(context = context) {
        productApi.apiProductGetAllUnitsGet()
    }
    private suspend fun addEditProductApi(product: Product, barcode: String?, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        productApi.apiProductAddEditProductPost(barcode, product)
    }
}