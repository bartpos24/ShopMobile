package pl.bartpos24.shopmobile.adapters

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.threeten.bp.format.DateTimeFormatter
import pl.bartpos24.shopmobile.databinding.CommonInventoryPositionItemBinding
import pl.bartpos24.web.model.CommonInventoryPosition

class CommonInventoryPositionListAdapter : ListAdapter<CommonInventoryPosition, CommonInventoryPositionListAdapter.ViewHolder>(CommonInventorypositionListDiffCallback()) {
    private val channel = MutableSharedFlow<CommonInventoryPosition>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    fun clicks() = channel.asSharedFlow()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CommonInventoryPositionItemBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { commonInventoryPosition ->
            with(holder) {
                itemView.tag = commonInventoryPosition
                bind(createOnClickListener(commonInventoryPosition), commonInventoryPosition)
            }
        }
    }
    private fun createOnClickListener(commonInventoryPosition: CommonInventoryPosition): View.OnClickListener =
        View.OnClickListener {
            channel.tryEmit(commonInventoryPosition)
        }
    class ViewHolder(private val binding: CommonInventoryPositionItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, commonInventoryPosition: CommonInventoryPosition) {
            with(binding) {
                productName.text = commonInventoryPosition.productName ?: ""
                quantity.text = commonInventoryPosition.quantity?.toString() ?: ""
                unit.text = commonInventoryPosition.unit?.name ?: ""
                price.text = commonInventoryPosition.price?.toString() ?: ""
                //scanDate.text = commonInventoryPosition.scanDate?.let { DateTimeFormatter.ofPattern("HH:mm:ss").format(it) } ?: ""
                scanDate.text = commonInventoryPosition.scanDate?.let { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(it) } ?: ""
            }
        }
    }
}
private class CommonInventorypositionListDiffCallback: DiffUtil.ItemCallback<CommonInventoryPosition>() {
    override fun areItemsTheSame(oldItem: CommonInventoryPosition, newItem: CommonInventoryPosition): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CommonInventoryPosition, newItem: CommonInventoryPosition): Boolean  =
        oldItem.id == newItem.id

}