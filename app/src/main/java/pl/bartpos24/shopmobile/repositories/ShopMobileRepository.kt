package pl.bartpos24.shopmobile.repositories

import kotlinx.coroutines.Dispatchers

open class ShopMobileRepository {
    open val coroutineContext = Dispatchers.IO
}