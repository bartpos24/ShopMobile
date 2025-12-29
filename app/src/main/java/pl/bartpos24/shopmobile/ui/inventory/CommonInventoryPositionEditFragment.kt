package pl.bartpos24.shopmobile.ui.inventory

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.format.DateTimeFormatter
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.databinding.CommonInventoryPositionEditFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.InventoryViewModel
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import kotlin.getValue

class CommonInventoryPositionEditFragment : ShopMobileFragment() {
    private val inventoryViewModel: InventoryViewModel by navGraphShopMobileViewModels(R.id.inventory_graph)
    private var binding: CommonInventoryPositionEditFragmentBinding by autoClearedView()

    private val args: CommonInventoryPositionEditFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonInventoryPositionEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inventoryViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        args.commonInventoryPosition.let {
            with(binding.editingPositionInformation) {
                productName.text = it.productName ?: ""
                unit.text = it.unit?.name ?: ""
                quantity.text = (it.quantity ?: 0.0).toString()
                price.text = (it.price ?: 0.0).toString()
                scanDate.text = it.scanDate?.let { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(it) } ?: ""
                editImgBtn.visibility = View.GONE
                deleteImgBtn.visibility = View.GONE

                binding.priceEditText.setText(it.price.toString())
                binding.quantityEditText.setText(it.quantity.toString())

                binding.quantityEditText.requestFocus()
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(binding.quantityEditText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.quantityEditText.textChanges()
            .debounce(200)
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
            .debounce(200)
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
            .map { inventoryViewModel.editingCommonInventoryPositionData(args.commonInventoryPosition) }
            .filter { it.id!! > 0 && it.inventoryId!! > 0 }
            .map { inventoryViewModel.editCommonInventoryPosition(it) }
            .onEach { binding.progress.visibility = View.GONE }
            .filter { it != null && (it.modifiedByUserId ?: 0) > 0 }
            .onEach { binding.editButton.isEnabled = false }
            .onEach { inventoryViewModel.clearData() }
            .debounce { 800 }
            .onEach { findNavController().popBackStack() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        combine(
            flow {
                emit((args.commonInventoryPosition.id == null || args.commonInventoryPosition.id!! <= 0)
                        || (args.commonInventoryPosition.inventoryId == null || args.commonInventoryPosition.inventoryId!! <= 0)) },
            inventoryViewModel.quantity.map { it == null || it <= 0.0 },
            inventoryViewModel.price.map { it == null || it <= 0.0 },
            combine(
                inventoryViewModel.price,
                inventoryViewModel.quantity
            ) { price, quantity ->
                price == args.commonInventoryPosition.price && quantity == args.commonInventoryPosition.quantity
            },
            transform = { inventoryPositionOk, quantityOk, priceOk, theSamePriceAndQuantity ->
                inventoryPositionOk || quantityOk || priceOk || theSamePriceAndQuantity
            }
        ).onEach { binding.editButton.isEnabled = !it }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

}