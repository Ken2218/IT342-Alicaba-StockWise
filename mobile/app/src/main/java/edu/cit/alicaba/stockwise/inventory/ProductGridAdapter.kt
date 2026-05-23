package edu.cit.alicaba.stockwise.inventory

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.alicaba.stockwise.R

class ProductGridAdapter(
    private var itemList: List<Item>,
    private val onEditClick: (Item) -> Unit
) : RecyclerView.Adapter<ProductGridAdapter.GridViewHolder>() {

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivProductImage)
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

        // --- CLEAN PRICE FORMATTING ---
        // If the price is a flat number (e.g., 20.0), chop off the decimal. Otherwise, keep 2 decimal places.
        val formattedPrice = if (item.price % 1.0 == 0.0) {
            item.price.toInt().toString()
        } else {
            String.format("%.2f", item.price)
        }
        holder.tvPrice.text = "₱$formattedPrice"

        // --- DYNAMIC STOCK COLORS ---
        if (item.quantity <= 0) {
            // Out of Stock (Red)
            holder.tvStock.text = "Out of stock"
            holder.tvStock.setBackgroundColor(Color.parseColor("#FEE2E2"))
            holder.tvStock.setTextColor(Color.parseColor("#991B1B"))
        } else if (item.quantity <= 5) {
            // Low Stock (Yellow)
            holder.tvStock.text = "${item.quantity} Low stock"
            holder.tvStock.setBackgroundColor(Color.parseColor("#FEF3C7"))
            holder.tvStock.setTextColor(Color.parseColor("#B45309"))
        } else {
            // In Stock (Green)
            holder.tvStock.text = "${item.quantity} In stock"
            holder.tvStock.setBackgroundColor(Color.parseColor("#D1FAE5"))
            holder.tvStock.setTextColor(Color.parseColor("#065F46"))
        }

        // --- IMAGE LOGIC ---
        if (item.imageBase64 != null && item.imageBase64.isNotEmpty()) {
            try {
                val decodedString = Base64.decode(item.imageBase64, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                holder.ivImage.setImageBitmap(decodedByte)
                holder.ivImage.setBackgroundColor(Color.TRANSPARENT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.ivImage.setImageDrawable(null)
            holder.ivImage.setBackgroundColor(Color.parseColor("#E5E7EB"))
        }

        holder.btnEdit.setOnClickListener { onEditClick(item) }
    }

    override fun getItemCount() = itemList.size

    fun updateData(newList: List<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }
}