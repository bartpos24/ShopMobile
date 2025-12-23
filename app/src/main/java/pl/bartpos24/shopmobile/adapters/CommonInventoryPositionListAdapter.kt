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
    private val channelEdit = MutableSharedFlow<CommonInventoryPosition>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val channelDelete = MutableSharedFlow<CommonInventoryPosition>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    fun editClicks() = channelEdit.asSharedFlow()
    fun deleteClicks() = channelDelete.asSharedFlow()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CommonInventoryPositionItemBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { commonInventoryPosition ->
            with(holder) {
                itemView.tag = commonInventoryPosition
                bind(createEditClickListener(commonInventoryPosition), createDeleteClickListener(commonInventoryPosition), commonInventoryPosition)
            }
        }
    }
    private fun createEditClickListener(commonInventoryPosition: CommonInventoryPosition): View.OnClickListener =
        View.OnClickListener {
            channelEdit.tryEmit(commonInventoryPosition)
        }
    private fun createDeleteClickListener(commonInventoryPosition: CommonInventoryPosition): View.OnClickListener =
        View.OnClickListener {
            channelDelete.tryEmit(commonInventoryPosition)
        }
    class ViewHolder(private val binding: CommonInventoryPositionItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(editListener: View.OnClickListener, deleteListener: View.OnClickListener, commonInventoryPosition: CommonInventoryPosition) {
            with(binding) {
                productName.text = commonInventoryPosition.productName ?: ""
                quantity.text = commonInventoryPosition.quantity?.toString() ?: ""
                unit.text = commonInventoryPosition.unit?.name ?: ""
                price.text = commonInventoryPosition.price?.toString() ?: ""
                //scanDate.text = commonInventoryPosition.scanDate?.let { DateTimeFormatter.ofPattern("HH:mm:ss").format(it) } ?: ""
                scanDate.text = commonInventoryPosition.scanDate?.let { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(it) } ?: ""

                editImgBtn.setOnClickListener(editListener)
                deleteImgBtn.setOnClickListener(deleteListener)
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