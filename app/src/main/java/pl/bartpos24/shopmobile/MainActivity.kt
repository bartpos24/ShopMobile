package pl.bartpos24.shopmobile

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.android.HasAndroidInjector
import dagger.android.DispatchingAndroidInjector
import kotlinx.coroutines.flow.MutableStateFlow
import pl.bartpos24.shopmobile.databinding.ActivityMainBinding
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.viewmodels.LoginViewModel
import androidx.lifecycle.asLiveData
import dagger.android.AndroidInjector
import pl.bartpos24.shopmobile.utilities.requestCodeRequiredPermissions
import pl.bartpos24.shopmobile.utilities.requiredPermissions
import pl.bartpos24.shopmobile.viewmodels.ShopMobileViewModelFactory
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasAndroidInjector, IActivityCommunicator {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //private val mainActivityViewModel: MainActivityViewModel by viewModels()
    //private lateinit var loginViewModel: LoginViewModel
    private var hasOptionsMenu: Boolean = false

    @Inject
    lateinit var shopMobileApp: ShopMobileApplication

    private val loginViewModel: LoginViewModel by viewModels { ShopMobileViewModelFactory(shopMobileApp.appComponent, this@MainActivity, null) }
    object loginAuth {
        private val status = MutableStateFlow<LoginStatus>(LoginStatus.UNAUTHENTICATED)
        fun getStatus() = status
        fun setStatus(status: LoginStatus) {
            this.status.value = status
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.findProductFragment, R.id.inventory_graph
            ),
            drawerLayout,
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        loginAuth.getStatus().asLiveData().observe(this@MainActivity) {
            if(it != LoginStatus.AUTHENTICATED) {
                loginViewModel.logout()
                findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
            }
        }

        if (!checkAllPermissions()) {
            ActivityCompat.requestPermissions(this, requiredPermissions, requestCodeRequiredPermissions)
        }
    }
    fun setHasOptionsMenu(value: Boolean) {
        hasOptionsMenu = value
    }

    override fun onResume() {
        super.onResume()
        loginAuth.getStatus().asLiveData().observe(this@MainActivity) {
            if(it != LoginStatus.AUTHENTICATED) {
                loginViewModel.logout()
                findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun androidInjector(): AndroidInjector<in Any> = androidInjector

    private fun checkAllPermissions() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == requestCodeRequiredPermissions) {
            if (!checkAllPermissions()) {
                Toast.makeText(this, "Required Permissions not granted by the user.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun alterToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    override fun setDrawerLockMode(lockMode: Int) {
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }
}
