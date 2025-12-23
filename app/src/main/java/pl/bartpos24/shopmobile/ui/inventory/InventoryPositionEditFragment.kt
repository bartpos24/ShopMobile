package pl.bartpos24.shopmobile.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.databinding.InventoryPositionEditFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.InventoryViewModel
import pl.bartpos24.web.model.InventoryPosition
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import kotlin.getValue

class InventoryPositionEditFragment : ShopMobileFragment() {
    private val inventoryViewModel: InventoryViewModel by navGraphShopMobileViewModels(R.id.inventory_graph)
    private var binding: InventoryPositionEditFragmentBinding by autoClearedView()
    private val args: InventoryPositionEditFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InventoryPositionEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inventoryViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        args.inventoryPosition.let {
            with(binding.editingPositionInformation) {
                productBarcode.text = it.product?.barcodes?.firstOrNull()?.code ?: ""
                productBrand.text = it.product?.brand ?: ""
                productName.text = it.product?.name ?: ""
                productCapacity.text = it.product?.capacity ?: ""
                unit.text = it.product?.unit?.name ?: ""
                quantity.text = (it.quantity ?: 0.0).toString()
                price.text = (it.price ?: 0.0).toString()
                scanDate.text = it.scanDate?.let { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(it) } ?: ""
                editImgBtn.visibility = View.GONE
                deleteImgBtn.visibility = View.GONE
            }
        }

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

        binding.editButton.clicks()
            .onEach { binding.progress.visibility = View.VISIBLE }
            .map { inventoryViewModel.editingInventoryPositionData(args.inventoryPosition) }
            .filter { it.id!! > 0 && it.productId!! > 0 && it.inventoryId!! > 0 }
            .map { inventoryViewModel.editInventoryPosition(it) }
            .onEach { binding.progress.visibility = View.GONE }
            .filter { it != null && (it.modifiedByUserId ?: 0) > 0 }
            .onEach { binding.editButton.isEnabled = false }
            .onEach { inventoryViewModel.clearData() }
            .debounce { 800 }
            .onEach { findNavController().popBackStack() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        combine(
            flow {
                emit((args.inventoryPosition.id == null || args.inventoryPosition.id!! <= 0)
                    || (args.inventoryPosition.productId == null || args.inventoryPosition.productId!! <= 0)
                        || (args.inventoryPosition.inventoryId == null || args.inventoryPosition.inventoryId!! <= 0)) },
            inventoryViewModel.quantity.map { it == null || it <= 0.0 },
            inventoryViewModel.price.map { it == null || it <= 0.0 },
            transform = { inventoryPositionOk, quantityOk, priceOk ->
                inventoryPositionOk || quantityOk || priceOk
            }
        ).onEach { binding.editButton.isEnabled = !it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.quantityEditText.requestFocus()
    }

}