package pl.bartpos24.shopmobile.ui.inventory

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.adapters.InventoryListAdapter
import pl.bartpos24.shopmobile.databinding.InventoryFragmentBinding
import pl.bartpos24.shopmobile.ui.ShopMobileFragment
import pl.bartpos24.shopmobile.utilities.MarginItemDecoration
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.utilities.navigateSafe
import pl.bartpos24.shopmobile.viewmodels.InventoryViewModel
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import kotlin.getValue

class InventoryFragment : ShopMobileFragment() {
    private val inventoryViewModel: InventoryViewModel by navGraphShopMobileViewModels(R.id.inventory_graph)
    private var binding: InventoryFragmentBinding by autoClearedView()
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InventoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inventoryViewModel.toastErrors(requireContext()).launchIn(viewLifecycleOwner.lifecycleScope)

        val adapter = InventoryListAdapter()
        binding.inventoryList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.default_padding)))
        binding.inventoryList.adapter = adapter

        //inventoryViewModel.getAllInventory()
        inventoryViewModel.inventories
            .onEach {
                adapter.submitList(it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        adapter.clicks()
            .filterNotNull()
            .onEach { inventoryViewModel.setInventory(it) }
            .map { inventoryViewModel.inventory }
            .filterNotNull()
            .onEach { findNavController().navigateSafe(InventoryFragmentDirections.actionInventoryFragmentToInventoryPositionFragment())}
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.inventorySwipeRefresh.refreshes()
            .onEach { inventoryViewModel.refreshInventory() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        inventoryViewModel.loading
            .onEach { binding.inventorySwipeRefresh.isRefreshing = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        inventoryViewModel.refreshInventory()
    }
}