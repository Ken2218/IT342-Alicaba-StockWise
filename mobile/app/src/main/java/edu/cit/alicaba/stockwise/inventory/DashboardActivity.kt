package edu.cit.alicaba.stockwise.inventory

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import edu.cit.alicaba.stockwise.R
import edu.cit.alicaba.stockwise.core.ApiClient
import edu.cit.alicaba.stockwise.core.ItemApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ItemApiService::class.java)

    // Track our complete list and our UI state
    private var allItemsList: List<Item> = emptyList()
    private var isGridView = true // Defaults to Product Grid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerViewInventory)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- THE TAB SWITCHING LOGIC ---
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_product_grid -> {
                    isGridView = true
                    refreshUI(allItemsList)
                }
                R.id.nav_quick_inventory -> {
                    isGridView = false
                    refreshUI(allItemsList)
                }
                R.id.nav_all_products -> filterCategory("")
                R.id.nav_beverages -> filterCategory("Beverages")
                R.id.nav_snacks -> filterCategory("Snacks")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // --- THE SEARCH BAR LOGIC ---
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText ?: ""
                val filteredList = allItemsList.filter {
                    it.name.contains(searchText, ignoreCase = true)
                }
                refreshUI(filteredList)
                return true
            }
        })

        findViewById<ExtendedFloatingActionButton>(R.id.fabAddItem).setOnClickListener { showAddItemDialog() }

        fetchInventory()
    }

    // --- THE MAGIC ADAPTER SWAPPER ---
    private fun refreshUI(listToDisplay: List<Item>) {
        if (isGridView) {
            recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 Columns
            recyclerView.adapter = ProductGridAdapter(listToDisplay) { item ->
                // You can add Edit logic here later! For now, let's just delete to prove it works
                showDeleteConfirmation(item)
            }
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this) // 1 Column List
            recyclerView.adapter = InventoryAdapter(listToDisplay,
                onStockChange = { item, newQty -> updateItemStock(item, newQty) },
                onItemLongClick = { item -> showDeleteConfirmation(item) }
            )
        }
    }

    private fun filterCategory(category: String) {
        if (category.isEmpty()) {
            refreshUI(allItemsList)
        } else {
            val filtered = allItemsList.filter { it.category.equals(category, ignoreCase = true) }
            refreshUI(filtered)
        }
    }

    private fun fetchInventory() {
        apiService.getAllItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful && response.body() != null) {
                    allItemsList = response.body()!!
                    refreshUI(allItemsList) // Loads the grid by default!
                }
            }
            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Keep your exact updateItemStock, showDeleteConfirmation, and showAddItemDialog functions here!
    private fun updateItemStock(item: Item, newQuantity: Int) {
        val updatedItem = item.copy(quantity = newQuantity)
        apiService.updateItem(item.id, updatedItem).enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful) fetchInventory()
            }
            override fun onFailure(call: Call<Item>, t: Throwable) {}
        })
    }

    // --- NEW FEATURE: DELETE ---
    private fun showDeleteConfirmation(item: Item) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete ${item.name}?")
            .setPositiveButton("Delete") { _, _ ->
                apiService.deleteItem(item.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) fetchInventory()
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- NEW FEATURE: ADD ---
    private fun showAddItemDialog() {
        // Create a simple layout for the inputs programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val nameInput = EditText(this).apply { hint = "Item Name" }
        val categoryInput = EditText(this).apply { hint = "Category" }
        val priceInput = EditText(this).apply { hint = "Price"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val qtyInput = EditText(this).apply { hint = "Quantity"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }

        layout.addView(nameInput)
        layout.addView(categoryInput)
        layout.addView(priceInput)
        layout.addView(qtyInput)

        AlertDialog.Builder(this)
            .setTitle("Add New Item")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val newItem = Item(
                    id = 0, // 0 tells Spring Boot to auto-generate a new ID
                    name = nameInput.text.toString(),
                    category = categoryInput.text.toString(),
                    price = priceInput.text.toString().toDoubleOrNull() ?: 0.0,
                    quantity = qtyInput.text.toString().toIntOrNull() ?: 0,
                    imageBase64 = null
                )
                apiService.createItem(newItem).enqueue(object : Callback<Item> {
                    override fun onResponse(call: Call<Item>, response: Response<Item>) {
                        if (response.isSuccessful) fetchInventory()
                    }
                    override fun onFailure(call: Call<Item>, t: Throwable) {}
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}