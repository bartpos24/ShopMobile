package pl.bartpos24.shopmobile.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.adapters.InventoryListAdapter
import pl.bartpos24.shopmobile.adapters.InventoryPositionListAdapter
import pl.bartpos24.shopmobile.databinding.InventoryPositionFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.MarginItemDecoration
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

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       inventoryViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        binding.cameraImgBtn.clicks()
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
            .onEach {
                binding.productBarcodeInputLayout.error = when {
                    binding.productBarcodeInputEditText.text.toString().isEmpty() -> getString(R.string.error_empty_field)
                    else -> null
                }
            }
            .filter { it.isNotEmpty() }
            .map { inventoryViewModel.getProductByBarcode(it.toString()) }
            .onEach {
                binding.productBarcodeInputLayout.error = when {
                    it == null -> getString(R.string.error_product_not_found_by_barcode)
                    else -> null
                }
            }
            .filterNotNull()
            .onEach { binding.barcodeScannerLayout.visibility = View.GONE }
            .onEach { binding.quantityEditText.requestFocus() }
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

        binding.quantityEditText.textChanges()
            .debounce(700)
            .map { it.toString().toDoubleOrNull() }
            .onEach { inventoryViewModel.setQuantity(it) }
            .onEach {
                binding.quantityInputLayout.error = when {
                    it == null -> getString(R.string.error_empty_field)
                    it <= 0.0 -> getString(R.string.error_zero_quantity)
                    else -> null
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.priceEditText.textChanges()
            .debounce(700)
            .map { it.toString().toDoubleOrNull() }
            .onEach { inventoryViewModel.setPrice(it) }
            .onEach {
                binding.priceInputLayout.error = when {
                    it == null -> getString(R.string.error_empty_field)
                    it <= 0.0 -> getString(R.string.error_zero_price)
                    else -> null
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.confirmButton.clicks()
            .onEach { binding.progress.visibility = View.VISIBLE }
            .map { inventoryViewModel.addInventoryPosition() }
            .onEach { binding.progress.visibility = View.GONE }
            .filter { it != null && (it.id ?: 0) > 0 }
            .onEach { binding.confirmButton.isEnabled = false }
            .onEach { clearData() }
            .onEach { inventoryViewModel.clearData() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        combine(
            inventoryViewModel.product.map { it == null },
            inventoryViewModel.quantity.map { it == null || it <= 0.0 },
            inventoryViewModel.price.map { it == null || it <= 0.0 },
            transform = { productOk, quantityOk, priceOk ->
                productOk || quantityOk || priceOk
            }
        ).onEach { binding.confirmButton.isEnabled = !it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val adapter = InventoryPositionListAdapter()
        binding.inventoryPositionList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.default_padding)))
        binding.inventoryPositionList.adapter = adapter

        inventoryViewModel.inventoryPositions
            .map { it.sortedByDescending { it.scanDate } }
            .onEach { adapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun clearData() {
        binding.productBarcodeInputEditText.setText("")
        binding.quantityEditText.setText("1.0")
        binding.priceEditText.setText("0.0")
    }
}