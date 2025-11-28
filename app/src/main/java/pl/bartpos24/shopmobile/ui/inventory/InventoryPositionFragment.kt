package pl.bartpos24.shopmobile.ui.inventory

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
import pl.bartpos24.shopmobile.databinding.InventoryPositionFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.InventoryViewModel
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges

class InventoryPositionFragment : ShopMobileFragment() {
    private val inventoryViewModel: InventoryViewModel by navGraphShopMobileViewModels(R.id.inventory_graph)

    private var binding: InventoryPositionFragmentBinding by autoClearedView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InventoryPositionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inventoryViewModel.scanner.stopScanning()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       inventoryViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        binding.btnCamera.clicks()
            .map { binding.barcodeScannerLayout.visibility }
            .onEach { inventoryViewModel.clearBarcodeData() }
            .onEach {
                if(it == View.VISIBLE) {
                    binding.barcodeScannerLayout.visibility = View.GONE
                    inventoryViewModel.scanner.stopScanning()
                } else {
                    binding.barcodeScannerLayout.visibility = View.VISIBLE
                    inventoryViewModel.scanner.startScanning(viewLifecycleOwner, binding.previewView)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        inventoryViewModel.barcodeResult
            .filter { it.isNotEmpty() }
            .onEach { binding.productBarcodeInputEditText.setText(it) }
            .onEach { inventoryViewModel.scanner.clearBarcodeResult() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productBarcodeInputEditText.textChanges()
            .debounce(700)
            .filter { it.isNotEmpty() }
            .map { inventoryViewModel.getProductByBarcode(it.toString()) }
            .filterNotNull()
            .onEach {
                binding.barcodeScannerLayout.visibility = View.GONE
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        inventoryViewModel.product
            .onEach {
                with(binding.productInformation) {
                    productName.text = it?.name ?: ""
                    productBarcode.text = it?.barcodes?.joinToString(", ") { it.code.toString() } ?: ""
                    productBrand.text = it?.brand ?: ""
                    productUnt.text = it?.unit?.name ?: ""
                    productCapacity.text = it?.capacity ?: ""
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}