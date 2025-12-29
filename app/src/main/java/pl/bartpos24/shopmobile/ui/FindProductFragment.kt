package pl.bartpos24.shopmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.adapters.ProductListAdapter
import pl.bartpos24.shopmobile.databinding.BarcodeScannerFragmentBinding
import pl.bartpos24.shopmobile.databinding.FindProductFragmentBinding
import pl.bartpos24.shopmobile.utilities.MarginItemDecoration
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.BarcodeScannerViewModel
import pl.bartpos24.shopmobile.viewmodels.ProductViewModel
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges

class FindProductFragment : ShopMobileFragment() {
    private val productViewModel: ProductViewModel by navGraphShopMobileViewModels(R.id.findProductFragment)

    private var binding: FindProductFragmentBinding by autoClearedView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindProductFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productViewModel.scanner.stopScanning()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        productViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        val productListAdapter = ProductListAdapter()
        binding.productList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.default_padding)))
        binding.productList.adapter = productListAdapter

        binding.cameraImgBtn.clicks()
            .map { binding.barcodeScannerLayout.visibility }
            .onEach { productViewModel.clearBarcodeData() }
            .onEach {
                if(it == View.VISIBLE) {
                    binding.barcodeScannerLayout.visibility = View.GONE
                    productViewModel.scanner.stopScanning()
                } else {
                    binding.barcodeScannerLayout.visibility = View.VISIBLE
                    productViewModel.scanner.startScanning(viewLifecycleOwner, binding.previewView)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // Sprawdzic czy kamera jest caly czas wlaczona nawet jesli widocznosc skanowania jest wylaczona
        productViewModel.barcodeResult
            .filter { it.isNotEmpty() }
            .onEach { binding.productBarcodeInputEditText.setText(it) }
            .onEach { productViewModel.clearBarcodeData() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productBarcodeInputEditText.textChanges()
            .debounce(700)
            .filter { it.isNotEmpty() }
            .map { productViewModel.getProductByBarcode(it.toString()) }
            .map {
                if(it.isNullOrEmpty())
                    productViewModel.getProductFromOpenFoodFacts(binding.productBarcodeInputEditText.text.toString())
                else it
            }
            .filterNotNull()
            .onEach {
                productListAdapter.submitList(it)
            }
            .onEach {
                binding.barcodeScannerLayout.visibility = View.GONE
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}