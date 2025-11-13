package pl.bartpos24.shopmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.databinding.SettingsFragmentBinding
import pl.bartpos24.shopmobile.models.ThemeMode
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.changeLocale
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.SettingsViewModel
import ru.ldralighieri.corbind.material.buttonCheckedChanges

class SettingsFragment : ShopMobileFragment() {
    private var binding: SettingsFragmentBinding by  autoClearedView()
    private val settingsViewModel: SettingsViewModel by navGraphShopMobileViewModels(R.id.settingsFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.userConfig
            //.onEach { binding.tieWebApiUrl.setText(it.webApiUrl) }
            .onEach {
                when (it.dayNightMode) {
                    ThemeMode.MODE_NIGHT -> binding.btnDarkMode.isChecked = true
                    ThemeMode.MODE_DAY -> binding.btnLightMode.isChecked = true
                    ThemeMode.MODE_FOLLOW_SYSTEM -> binding.btnFollowSystemMode.isChecked = true
                }
            }
            .onEach {
                when (it.languageCode) {
                    "pl" -> binding.btnLanguagePl.isChecked = true
                    "en" -> binding.btnLanguageEn.isChecked = true
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.tbLightDarkMode.buttonCheckedChanges()
            .debounce(250)
            .mapNotNull {
                when (it) {
                    binding.btnDarkMode.id -> ThemeMode.MODE_NIGHT
                    binding.btnLightMode.id -> ThemeMode.MODE_DAY
                    binding.btnFollowSystemMode.id -> ThemeMode.MODE_FOLLOW_SYSTEM
                    else -> ThemeMode.MODE_NIGHT
                }
            }
            .onEach { settingsViewModel.saveUserConfig(it) }
            .map {
                when (it) {
                    ThemeMode.MODE_NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
                    ThemeMode.MODE_DAY -> AppCompatDelegate.MODE_NIGHT_NO
                    ThemeMode.MODE_FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
            .onEach { AppCompatDelegate.setDefaultNightMode(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.tbLanguages.buttonCheckedChanges()
            .debounce(250)
            .mapNotNull {
                when (it) {
                    binding.btnLanguagePl.id -> "pl"
                    binding.btnLanguageEn.id -> "en"
                    else -> "pl"
                }
            }
            .onEach { requireActivity().changeLocale(it, requireContext(), viewLifecycleOwner.lifecycleScope) }
            .onEach { settingsViewModel.saveUserConfigLang(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}