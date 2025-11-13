package pl.bartpos24.shopmobile.utilities

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

fun Activity.changeLocale(languageCode: String, context: Context, lifecycleScope: LifecycleCoroutineScope) {
    val currentLocale = this.resources.configuration.locales.get(0).language
    if (currentLocale == languageCode)
        return

    Locale(languageCode).let { locale ->
        Locale.setDefault(locale)
        context.resources.configuration.let { config ->
            config.setLocale(locale)
            context.resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
    lifecycleScope.launch {
        delay(1000)
        this@changeLocale.recreate() // recreate activity so all resources will be updated
    }
}