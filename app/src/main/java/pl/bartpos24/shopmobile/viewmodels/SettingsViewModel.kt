package pl.bartpos24.shopmobile.viewmodels

import pl.bartpos24.shopmobile.models.ThemeMode
import pl.bartpos24.shopmobile.repositories.UserConfigRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val userConfigRepository: UserConfigRepository) : ShopMobileViewModel() {
    val userConfig = userConfigRepository.userConfig

    suspend fun saveUserConfig(mode: ThemeMode) = userConfigRepository.saveConfig(userConfig.value.copy(dayNightMode = mode))

    suspend fun saveUserConfigLang(languageCode: String) = userConfigRepository.saveConfig(userConfig.value.copy(languageCode = languageCode))
}