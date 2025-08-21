package pl.bartpos24.shopmobile.dagger

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import pl.bartpos24.shopmobile.ShopMobileApplication
import pl.bartpos24.shopmobile.workers.ShopMobileWorkerFactory

object AppInjector {
    fun init(shopMobileApp: ShopMobileApplication): AppComponent {
        val daggerComponent = DaggerAppComponent
            .factory()
            .create(shopMobileApp)

        daggerComponent.inject(shopMobileApp)

        val configuration = Configuration.Builder()
            .setWorkerFactory(ShopMobileWorkerFactory(daggerComponent))
            .build()
        WorkManager.initialize(shopMobileApp, configuration)

        // TODO: Switch to new lifecycle handling(lifecycle owner), make this lifecycle aware
        shopMobileApp.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
        return daggerComponent
    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasAndroidInjector) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        savedInstanceState: Bundle?
                    ) {
                        if (f is Injectable) {
                            AndroidSupportInjection.inject(f)
                        }
                    }
                },
                true
            )
        }
    }
}