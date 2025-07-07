package pl.bartpos24.shopmobile.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
open class ShopMobileViewModel : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    protected fun offerError(message: String) { _error.value = message }

    private val _errorResId = MutableStateFlow<Int?>(null)
    protected fun offerError(resId: Int) { _errorResId.value = resId }

    fun toastErrors(context: Context) = merge(
        _errorResId,
        _error
    )
        .filterNotNull()
        .mapNotNull {
            when (it) {
                is Int -> context.resources.getString(it)
                is String -> it
                else -> null
            }
        }
        .onEach { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        .onEach {
            _errorResId.value = null
            _error.value = null
        }
        .onEach { delay(1500) }
}