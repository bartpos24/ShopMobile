package pl.bartpos24.shopmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
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
import pl.bartpos24.shopmobile.viewmodels.FindProductViewModel
import ru.ldralighieri.corbind.view.clicks

class FindProductFragment : ShopMobileFragment() {
    private val findProductViewModel: FindProductViewModel by navGraphShopMobileViewModels(R.id.scanner_product_graph)
    private val barcodeScannerViewModel: BarcodeScannerViewModel by navGraphShopMobileViewModels(R.id.scanner_product_graph)

    private var binding: FindProductFragmentBinding by autoClearedView()

    private var bindingBarcodeScanner: BarcodeScannerFragmentBinding by autoClearedView()

    private lateinit var barcodeScannerFragment: BarcodeScannerFragment
    private val barcodeScannerFragmentTag = "BarcodeScannerFragmentTag"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindProductFragmentBinding.inflate(inflater, container, false)
        bindingBarcodeScanner = BarcodeScannerFragmentBinding.inflate(inflater, container, false)
        initBarcodeScannerfragment(R.id.scanner_product_graph)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findProductViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        val productListAdapter = ProductListAdapter()
        binding.productList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.default_padding)))
        binding.productList.adapter = productListAdapter

        binding.btnCamera.clicks()
            .onEach { barcodeScannerViewModel.clearData() }
            .onEach { binding.barcodeScannerLayout.visibility = View.VISIBLE }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        // Sprawdzic czy kamera jest caly czas wlaczona nawet jesli widocznosc skanowania jest wylaczona
        barcodeScannerViewModel.barcodeResults
            .mapNotNull { it }
            .onEach {
                var x = it
            }
            .map { findProductViewModel.getProductByBarcode(it.rawValue) }
            .onEach {
                var x = it
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

    private fun initBarcodeScannerfragment(viewRId: Int) {
        var oldBarcodeScannerFragment = childFragmentManager.findFragmentByTag(barcodeScannerFragmentTag)
        barcodeScannerFragment = BarcodeScannerFragment(requireContext(), bindingBarcodeScanner, viewRId)

        childFragmentManager.commit {
            if(oldBarcodeScannerFragment == null)
                add(R.id.barcodeScannerFragmentContainer, barcodeScannerFragment, barcodeScannerFragmentTag)
            else
                replace(R.id.barcodeScannerFragmentContainer, barcodeScannerFragment, barcodeScannerFragmentTag)
        }
    }
}