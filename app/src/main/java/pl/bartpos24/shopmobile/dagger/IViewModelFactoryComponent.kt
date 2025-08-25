package pl.bartpos24.shopmobile.dagger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Provider

@Subcomponent(modules = [ViewModelModule::class])
interface IViewModelFactoryComponent {
    fun creators(): Map<Class<*>, @JvmSuppressWildcards Provider<ViewModel>>

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance key: String, @BindsInstance handle: SavedStateHandle): IViewModelFactoryComponent
    }
}