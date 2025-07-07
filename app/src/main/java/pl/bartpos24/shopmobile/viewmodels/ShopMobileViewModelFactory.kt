package pl.bartpos24.shopmobile.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import pl.bartpos24.shopmobile.dagger.IAppComponent
import java.lang.IllegalArgumentException
import javax.inject.Provider

class ShopMobileViewModelFactory(private val appComponent: IAppComponent, owner: SavedStateRegistryOwner, defaultArgs: Bundle?) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    private fun <T : ViewModel?> createViewModel(modelClass: Class<T>, creators: Map<Class<*>, @JvmSuppressWildcards Provider<ViewModel>>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T = appComponent
        .viewModelFactoryComponent()
        .create(key, handle)
        .run { createViewModel(modelClass, creators()) }
}