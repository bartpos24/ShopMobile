package pl.bartpos24.shopmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.bartpos24.shopmobile.databinding.ProductItemBinding
import pl.bartpos24.web.model.Product

class ProductListAdapter: ListAdapter<Product, ProductListAdapter.ViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { product ->
            with(holder) {
                itemView.tag = product
                bind(product)
            }
        }
    }
    class ViewHolder(private val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) {
            with(binding) {
                productName.text = item.name
                productBarcode.text = item.barcodes?.joinToString(", ") { it.code.toString() }
                productBrand.text = item.brand
                productUnt.text = item.unit?.name
                productCapacity.text = item.capacity
            }
        }
    }
}

private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}