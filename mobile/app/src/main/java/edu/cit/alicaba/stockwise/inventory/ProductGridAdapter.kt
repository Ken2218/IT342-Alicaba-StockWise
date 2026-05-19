package edu.cit.alicaba.stockwise.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.alicaba.stockwise.R

class ProductGridAdapter(
    private var itemList: List<Item>,
    private val onEditClick: (Item) -> Unit
) : RecyclerView.Adapter<ProductGridAdapter.GridViewHolder>() {

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCardCategory)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCardPrice)
        val tvStock: TextView = itemView.findViewById(R.id.tvCardStock)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_card, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = itemList[position]
        holder.tvName.text = item.name
        holder.tvCategory.text = item.category
        holder.tvPrice.text = "₱${item.price}"
        holder.tvStock.text = "${item.quantity} In stock"

        holder.btnEdit.setOnClickListener { onEditClick(item) }
    }

    override fun getItemCount() = itemList.size

    fun updateData(newList: List<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }
}