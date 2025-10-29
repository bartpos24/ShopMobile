package pl.bartpos24.shopmobile.viewmodels

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pl.bartpos24.shopmobile.models.BarcodeResult
import javax.inject.Inject

class BarcodeScannerViewModel @Inject constructor() : ShopMobileViewModel() {
    private val _barcodeResults = MutableSharedFlow<BarcodeResult?>(replay = 0)
    val barcodeResults: SharedFlow<BarcodeResult?> = _barcodeResults.asSharedFlow()

    fun clearData() {
        _barcodeResults.tryEmit(null)
    }

    suspend fun emitBarcodeResult(result: BarcodeResult) {
        _barcodeResults.emit(result)
    }
}