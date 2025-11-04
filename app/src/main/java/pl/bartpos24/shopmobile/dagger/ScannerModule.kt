package pl.bartpos24.shopmobile.dagger

import dagger.Module
import dagger.Provides
import pl.bartpos24.shopmobile.ShopMobileApplication
import pl.bartpos24.shopmobile.scanner.Scanner
import javax.inject.Singleton

@Module
object ScannerModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideScanner(applicationContext: ShopMobileApplication): Scanner = Scanner(applicationContext)
}