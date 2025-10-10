package pl.bartpos24.shopmobile.utilities

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import kotlin.properties.ReadWriteProperty
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KProperty

class AutoClearedViewValue<T: Any>(val fragment: Fragment) : ReadWriteProperty<Fragment, T>, LifecycleObserver {
    private var _value: T? = null
    init {
        fragment.viewLifecycleOwnerLiveData.asFlow()
            .filterNotNull()
            .onEach { it.lifecycle.addObserver(this) }
            .launchIn(fragment.lifecycleScope)
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroyView() {
//        _value = null
//    }
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )
    }
    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        _value = value
    }
}
fun <T : Any> Fragment.autoClearedView() = AutoClearedViewValue<T>(this)