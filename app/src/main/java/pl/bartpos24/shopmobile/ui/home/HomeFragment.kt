package pl.bartpos24.shopmobile.ui.home

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.map
import pl.bartpos24.shopmobile.databinding.FragmentHomeBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.navigateSafe
import pl.bartpos24.shopmobile.viewmodels.LoginViewModel
import ru.ldralighieri.corbind.view.clicks

class HomeFragment : ShopMobileFragment() {
    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        loginViewModel = getViewModel(LoginViewModel::class.java, this)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loginViewModel.toastErrors(requireContext())
            .launchIn(viewLifecycleOwner.lifecycleScope)

        loginViewModel.loginError
            .filterNotNull()
            .onEach { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.loginButton.clicks()
            .debounce(1000)
            .map {
                loginViewModel.refreshToken()
            }
            .onEach {
                var x = it
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.testButton.clicks()
            .debounce(1000)
            .map {
                HomeFragmentDirections.actionHomeFragmentToScannerProductGraph()
            }
            .onEach {
                findNavController().navigateSafe(it)
            }
//            .map { loginViewModel.getProductByBarcode("5906340630011") }
//            .onEach {
//                var x = it
//            }
//            .map { loginViewModel.refreshAccessToken() }
//            .onEach {
//                var x = it
//            }
            //.map { loginViewModel.getProductFromOpenFoodFacts("5906340630011") }
            .onEach {
                var x = it
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
