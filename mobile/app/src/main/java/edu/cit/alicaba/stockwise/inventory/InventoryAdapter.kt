package edu.cit.alicaba.stockwise.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.alicaba.stockwise.R

class InventoryAdapter(
    private var itemList: List<Item>,
    private val onStockChange: (Item, Int) -> Unit,
    private val onItemLongClick: (Item) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvItemCategory)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory_row, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = itemList[position]
        holder.tvName.text = item.name
        holder.tvCategory.text = item.category
        holder.tvQuantity.text = item.quantity.toString()

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 0) onStockChange(item, item.quantity - 1)
        }// Trigger the delete popup on long press
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true // Tells Android the long-click was handled
        }
        holder.btnPlus.setOnClickListener {
            onStockChange(item, item.quantity + 1)
        }
    }

    override fun getItemCount() = itemList.size

    fun updateData(newList: List<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }
}