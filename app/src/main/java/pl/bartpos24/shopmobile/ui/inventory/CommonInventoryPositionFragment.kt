package pl.bartpos24.shopmobile.ui.inventory

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.adapters.DropdownListAdapter
import pl.bartpos24.shopmobile.databinding.CommonInventoryPositionFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.InventoryViewModel
import pl.bartpos24.web.model.Unit
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import kotlin.getValue

class CommonInventoryPositionFragment : ShopMobileFragment() {
    private val inventoryViewModel: InventoryViewModel by navGraphShopMobileViewModels(R.id.inventory_graph)
    private var binding: CommonInventoryPositionFragmentBinding by autoClearedView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonInventoryPositionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inventoryViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        val unitsDropdownAdapter = DropdownListAdapter<Unit>(
            requireContext(), convertToString = { it.code },
            convertFromString = { item, items -> items.singleOrNull { it.code == item } }
        ) { item, binding ->
            binding.objectLabel.text = item.code
        }
        binding.unitsExposedDropdown.textChanges()
            .map { it.toString() }
            .map { binding.unitsExposedDropdown.text.toString() }
            .filter { it.isNotEmpty() }
            .mapNotNull { inventoryViewModel.units.value.firstOrNull { u -> u.code == it } }
            .onEach { inventoryViewModel.setUnit(it) }
            .onEach { binding.priceEditText.requestFocus() }
            .catch { }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        with(binding.unitsExposedDropdown) {
            setAdapter(unitsDropdownAdapter)
            inputType = InputType.TYPE_CLASS_TEXT
        }

        inventoryViewModel.unit
            .onEach {
                with(binding.unitsExposedDropdown) {
                    if (text.toString() != it?.code)
                        setText(it?.code, false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        inventoryViewModel.units
            .onEach { unitsDropdownAdapter.submitList(it.filter { conv -> conv.id != null }) }
            .map { inventoryViewModel.units.value.firstOrNull{ t -> t.code == "szt"}  }
            .onEach {
                inventoryViewModel.setUnit(it ?: inventoryViewModel.units.value.firstOrNull())
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.productNameInputEditText.textChanges()
            .debounce(700)
            .map { it.toString() }
            .onEach { inventoryViewModel.setProductName(it) }
            .onEach {
                binding.quantityInputLayout.error = when {
                    it.isEmpty() -> getString(R.string.error_empty_field)
                    else -> null
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
            .map { inventoryViewModel.addCommonInventoryPosition() }
            .onEach { binding.progress.visibility = View.GONE }
            .filter { it != null && (it.id ?: 0) > 0 }
            .onEach { binding.confirmButton.isEnabled = false }
            .onEach { inventoryViewModel.clearData() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        combine(
            inventoryViewModel.productName.map { it.isNullOrEmpty() },
            inventoryViewModel.quantity.map { it == null || it <= 0.0 },
            inventoryViewModel.price.map { it == null || it <= 0.0 },
            inventoryViewModel.unit.map { it == null },
            transform = { productOk, quantityOk, priceOk, unitOk ->
                productOk || quantityOk || priceOk || unitOk
            }
        ).onEach { binding.confirmButton.isEnabled = !it }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun clearData() {
        binding.productNameInputEditText.setText("")
        binding.quantityEditText.setText("1.0")
        binding.priceEditText.setText("0.0")
    }
}