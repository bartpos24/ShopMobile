package pl.bartpos24.shopmobile.adapters

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pl.bartpos24.shopmobile.databinding.InventoryPositionItemBinding
import pl.bartpos24.web.model.InventoryPosition

class InventoryPositionListAdapter : ListAdapter<InventoryPosition, InventoryPositionListAdapter.ViewHolder>(InventorypositionListDiffCallback()) {
    private val channel = MutableSharedFlow<InventoryPosition>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    fun clicks() = channel.asSharedFlow()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(InventoryPositionItemBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { inventoryPosition ->
            with(holder) {
                itemView.tag = inventoryPosition
                bind(createOnClickListener(inventoryPosition), inventoryPosition)
            }
        }
    }
    private fun createOnClickListener(inventoryPosition: InventoryPosition): View.OnClickListener =
        View.OnClickListener {
            channel.tryEmit(inventoryPosition)
        }
    class ViewHolder(private val binding: InventoryPositionItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, inventoryPosition: InventoryPosition) {
            with(binding) {
                productBrand.text = inventoryPosition.product?.brand ?: ""
                productBarcode.text = inventoryPosition.product?.barcodes?.firstOrNull()?.code ?: ""
                productName.text = inventoryPosition.product?.name ?: ""
                productCapacity.text = inventoryPosition.product?.capacity ?: ""
            }
        }
    }
}
private class InventorypositionListDiffCallback: DiffUtil.ItemCallback<InventoryPosition>() {
    override fun areItemsTheSame(oldItem: InventoryPosition, newItem: InventoryPosition): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: InventoryPosition, newItem: InventoryPosition): Boolean  =
        oldItem.id == newItem.id

}