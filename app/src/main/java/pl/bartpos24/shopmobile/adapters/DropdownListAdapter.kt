package pl.bartpos24.shopmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filter.FilterResults
import pl.bartpos24.shopmobile.R
import pl.bartpos24.shopmobile.databinding.DropdownMenuPopupItemBinding

class DropdownListAdapter<T>(
    context: Context,
    private val convertToString: (item: T) -> String? = { it.toString() },
    private val convertFromString: (item: String, items: List<T>) -> T? = { i: String, items: List<T> -> items.singleOrNull { it.toString() == i } },
    private val items: MutableList<T> = mutableListOf(),
    private val filter: (item: T, value: CharSequence) -> Boolean = { _, _ -> true },
    private val bind: (item: T, binding: DropdownMenuPopupItemBinding) -> Unit
) : ArrayAdapter<T>(context, R.layout.dropdown_menu_popup_item, items) {
    val sourceItems = items.toMutableList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            DropdownMenuPopupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            DropdownMenuPopupItemBinding.bind(convertView)
        }
        getItem(position)?.let {
            bind(it, binding)
        }
        return binding.root
    }
    fun getItem(item: String) = convertFromString(item, items)

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults = FilterResults().apply {
            val filteredItems = if (constraint == null) sourceItems else sourceItems.filter { item -> filter(item, constraint) }
            values = filteredItems
            count = filteredItems.size
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
            @Suppress("UNCHECKED_CAST")
            (results?.values as List<T>).let {
                clear()
                addAll(it)
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return if (resultValue == null)
                ""
            else
                @Suppress("UNCHECKED_CAST")
                return convertToString(resultValue as T) ?: ""
        }
    }

    fun submitList(list: List<T>) {
        sourceItems.clear()
        sourceItems.addAll(list)
        items.clear()
        items.addAll(list)
    }
}