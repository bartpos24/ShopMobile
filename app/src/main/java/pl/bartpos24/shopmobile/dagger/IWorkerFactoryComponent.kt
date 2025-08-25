package pl.bartpos24.shopmobile.dagger

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Provider

@Subcomponent(modules = [WorkerBuildersModule::class])
interface IWorkerFactoryComponent {
    fun creators(): Map<Class<*>, @JvmSuppressWildcards Provider<ListenableWorker>>
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context, @BindsInstance workerParameters: WorkerParameters): IWorkerFactoryComponent
    }
}