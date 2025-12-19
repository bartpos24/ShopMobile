package pl.bartpos24.shopmobile.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.threeten.bp.format.DateTimeFormatter
import pl.bartpos24.shopmobile.databinding.InventoryItemBinding
import pl.bartpos24.web.model.Inventory

class InventoryListAdapter : ListAdapter<Inventory, InventoryListAdapter.ViewHolder>(InventoryDiffCallback()) {

    private val channel = MutableSharedFlow<Inventory>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    fun clicks() = channel.asSharedFlow()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(InventoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { inventory ->
            with(holder) {
                itemView.tag = inventory
                bind(createOnClickListener(inventory), inventory)
            }
        }
    }

    private fun createOnClickListener(inventory: Inventory): View.OnClickListener =
        View.OnClickListener {
            channel.tryEmit(inventory)
        }

    class ViewHolder(private val binding: InventoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bind(listener: View.OnClickListener, inventory: Inventory) {
            with(binding) {
                name.text = inventory.name
                companyInformation.text = if(!inventory.companyName.isNullOrEmpty() && !inventory.companyAddress.isNullOrEmpty())  ((inventory.companyName ?: "") + "\n" + (inventory.companyAddress ?: "")) else  ""//String.format("%s, %d", inventory.companyName ?: "", inventory.companyAddress ?: "")//"${inventory.companyName ?: ""}, ${inventory.companyAddress}"
                type.text = inventory.type ?: ""
                executeWay.text = inventory.executeWay ?: ""
                creationDate.text = inventory.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
                createdByUser.text = (inventory.createdByUser?.name ?: "") + " " + (inventory.createdByUser?.surname ?: "")
                status.text = inventory.inventoryStatus?.name ?: ""
                cardConstraintLayout.setOnClickListener(listener)
            }
        }
    }
}

private class InventoryDiffCallback : DiffUtil.ItemCallback<Inventory>() {
    override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.createdByUserId == newItem.createdByUserId &&
                oldItem.inventoryStatusId == newItem.inventoryStatusId &&
                oldItem.executeWay == newItem.executeWay &&
                oldItem.type == newItem.type
    }

    override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean =
        oldItem.id == newItem.id
}