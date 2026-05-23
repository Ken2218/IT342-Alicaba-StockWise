package edu.cit.alicaba.stockwise.inventory

import android.graphics.Color
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
        val tvName: TextView = itemView.findViewById(R.id.tvRowName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvRowCategory)
        val tvQty: TextView = itemView.findViewById(R.id.tvRowQty)

        // NEW: Grab the stock tag we just added to the XML
        val tvStock: TextView = itemView.findViewById(R.id.tvRowStock)

        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory_row, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = itemList[position]
        holder.tvName.text = item.name
        holder.tvCategory.text = item.category
        holder.tvQty.text = item.quantity.toString()

        // --- THE DYNAMIC STOCK COLORS ---
        if (item.quantity <= 0) {
            holder.tvStock.text = "Out of stock"
            holder.tvStock.setBackgroundColor(Color.parseColor("#FEE2E2"))
            holder.tvStock.setTextColor(Color.parseColor("#991B1B"))
        } else if (item.quantity <= 5) {
            holder.tvStock.text = "Low stock"
            holder.tvStock.setBackgroundColor(Color.parseColor("#FEF3C7"))
            holder.tvStock.setTextColor(Color.parseColor("#B45309"))
        } else {
            holder.tvStock.text = "In stock"
            holder.tvStock.setBackgroundColor(Color.parseColor("#D1FAE5"))
            holder.tvStock.setTextColor(Color.parseColor("#065F46"))
        }

        // Handle the + and - clicks
        holder.btnPlus.setOnClickListener {
            onStockChange(item, item.quantity + 1)
        }

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 0) {
                onStockChange(item, item.quantity - 1)
            }
        }

        // Trigger the delete popup on long press
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }

    override fun getItemCount() = itemList.size

    fun updateData(newList: List<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }
}