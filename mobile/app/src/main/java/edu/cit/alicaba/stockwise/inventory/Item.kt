package edu.cit.alicaba.stockwise.inventory

data class Item(
    val id: Long,
    val name: String,
    val category: String,
    val price: Double,
    var quantity: Int,
    val imageBase64: String?
)