package pl.bartpos24.shopmobile.ui.product

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
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
            //.map { productViewModel.addEditProduct() }
            .onEach { binding.progress.visibility = View.GONE }
            //.filter { it != null && (it.id ?: 0) > 0 }
            .onEach { binding.confirmButton.isEnabled = false }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}