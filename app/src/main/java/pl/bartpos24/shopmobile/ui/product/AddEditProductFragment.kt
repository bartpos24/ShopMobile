package pl.bartpos24.shopmobile.ui.product

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.adapters.DropdownListAdapter
import pl.bartpos24.shopmobile.databinding.AddEditProductFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.ProductViewModel
import pl.bartpos24.web.model.ProductUnit
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import kotlin.getValue

class AddEditProductFragment : ShopMobileFragment() {
    private val productViewModel: ProductViewModel by navGraphShopMobileViewModels(R.id.addEditProductFragment)
    private var binding: AddEditProductFragmentBinding by autoClearedView()
    private val args: AddEditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEditProductFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        productViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        productViewModel.setProduct(args.product)
        binding.productBarcodeInputEditText.setText(args.barcode ?: "")

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

        productViewModel.barcodeResult
            .filter { it.isNotEmpty() }
            .onEach { binding.productBarcodeInputEditText.setText(it) }
            .onEach { productViewModel.scanner.clearBarcodeResult() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productBarcodeInputEditText.textChanges()
            .debounce(700)
            .onEach {
                binding.productBarcodeInputLayout.error = when {
                    binding.productBarcodeInputEditText.text.toString().isEmpty() -> getString(R.string.error_empty_field)
                    else -> null
                }
            }
            .onEach { productViewModel.setProductBarcode(it.toString()) }
            .filter { it.isNotEmpty() }
            .map { productViewModel.getProductByBarcode(it.toString()) }
            .filterNotNull()
            .onEach { binding.barcodeScannerLayout.visibility = View.GONE }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productBrandInputEditText.textChanges()
            .debounce(700)
            .onEach { productViewModel.setProductBrand(it.toString()) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productNameInputEditText.textChanges()
            .debounce(700)
            .onEach { productViewModel.setProductName(it.toString()) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productCapacityInputEditText.textChanges()
            .debounce(700)
            .onEach { productViewModel.setProductCapacity(it.toString()) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        productViewModel.product
            .onEach { binding.productInformation.root.visibility = if(it != null) View.VISIBLE else View.GONE }
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

        val unitsDropdownAdapter = DropdownListAdapter<ProductUnit>(
            requireContext(), convertToString = { it.code },
            convertFromString = { item, items -> items.singleOrNull { it.code == item } }
        ) { item, binding ->
            binding.objectLabel.text = item.code
        }
        binding.unitsExposedDropdown.textChanges()
            .map { it.toString() }
            .map { binding.unitsExposedDropdown.text.toString() }
            .filter { it.isNotEmpty() }
            .mapNotNull { productViewModel.units.value.firstOrNull { u -> u.code == it } }
            .onEach { productViewModel.setUnit(it) }
            .catch { }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        with(binding.unitsExposedDropdown) {
            setAdapter(unitsDropdownAdapter)
            inputType = InputType.TYPE_CLASS_TEXT
        }

        productViewModel.unit
            .onEach {
                with(binding.unitsExposedDropdown) {
                    if (text.toString() != it?.code)
                        setText(it?.code, false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        productViewModel.units
            .onEach { unitsDropdownAdapter.submitList(it.filter { conv -> conv.id != null }) }
            .map { productViewModel.units.value.firstOrNull{ t -> t.code == "szt"}  }
            .onEach {
                productViewModel.setUnit(it ?: productViewModel.units.value.firstOrNull())
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.confirmButton.clicks()
            .onEach { binding.progress.visibility = View.VISIBLE }
            .map { productViewModel.getProductData() }
            .map { productViewModel.addEditProduct(it) }
            .onEach { binding.progress.visibility = View.GONE }
            .filter { it != null && it > 0 }
            .onEach { clearData() }
            .onEach { productViewModel.clearData() }
            .onEach { if (args.product != null) findNavController().popBackStack() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun clearData() {
        binding.productBarcodeInputEditText.setText("")
        binding.productBrandInputEditText.setText("")
        binding.productNameInputEditText.setText("")
        binding.productCapacityInputEditText.setText("")
    }
}