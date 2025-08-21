package pl.bartpos24.shopmobile.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Provider
import pl.bartpos24.shopmobile.dagger.AppComponent
class ShopMobileWorkerFactory(private val appComponent: AppComponent) : WorkerFactory() {

    private fun createWorker(workerClassName: String, creators: Map<Class<*>, @JvmSuppressWildcards Provider<ListenableWorker>>): ListenableWorker? {
        val workerClass = Class.forName(workerClassName)
        val creator = creators[workerClass] ?: creators.entries.firstOrNull {
            workerClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $workerClass")
        try {
            return creator.get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters) = appComponent
        .workerFactoryComponent()
        .create(appContext, workerParameters)
        .run { createWorker(workerClassName, creators()) }
}