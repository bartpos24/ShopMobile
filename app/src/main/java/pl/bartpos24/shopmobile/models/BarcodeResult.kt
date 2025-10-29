package pl.bartpos24.shopmobile.models

data class BarcodeResult(
    val rawValue: String,
    val format: Int,
    val timestamp: Long = System.currentTimeMillis()
)