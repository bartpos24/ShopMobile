package pl.bartpos24.shopmobile.dagger

import androidx.work.ListenableWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import pl.bartpos24.shopmobile.workers.RefreshTokenWorker

@Module
abstract class WorkerBuildersModule {
    @Binds
    @IntoMap
    @ClassKey(RefreshTokenWorker::class)
    abstract fun bindRefreshTokenWorker(refreshTokenWorker: RefreshTokenWorker): ListenableWorker
}