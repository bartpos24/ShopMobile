package pl.bartpos24.shopmobile.ui

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.IActivityCommunicator
import pl.bartpos24.shopmobile.dagger.Injectable
import pl.bartpos24.shopmobile.databinding.LoginFragmentBinding
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.states
import pl.bartpos24.shopmobile.viewmodels.LoginViewModel
import ru.ldralighieri.corbind.view.clicks

class LoginFragment : ShopMobileFragment(), Injectable {
    private val loginViewModel: LoginViewModel by activityViewModels()
    private var binding: LoginFragmentBinding by autoClearedView()
//    private var _binding: LoginFragmentBinding? = null
//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        loginViewModel.toastErrors(requireContext())
            .launchIn(viewLifecycleOwner.lifecycleScope)

        loginViewModel.loginError
            .filterNotNull()
            .onEach { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.loginButton.clicks()
            .debounce(1000)
            .onEach {
                with(binding) {
                    loginViewModel.login(
                        "temp_user",
                        "temp_password",
                        Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                    )
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        loginViewModel.loginInProgress.observe(viewLifecycleOwner) {
            with(binding) {
                loginProgressBar.isInvisible = !it
                loginButton.isInvisible = it
            }
        }
        loginViewModel.authenticationState.observe(viewLifecycleOwner) {
            if (it == LoginStatus.AUTHENTICATED) findNavController().popBackStack()
        }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = true) { it.finish() }
        }

        viewLifecycleOwner.lifecycle.states()
            .onEach {
                (activity as? IActivityCommunicator)?.apply {
                    val drawerMode = when (it) {
                        Lifecycle.State.DESTROYED -> DrawerLayout.LOCK_MODE_UNLOCKED
                        else -> {
                            alterToolbar()
                            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                        }
                    }
                    setDrawerLockMode(drawerMode)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}