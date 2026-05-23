package edu.cit.alicaba.stockwise.inventory

import android.os.Bundle
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
import com.google.android.material.textfield.TextInputEditText
import edu.cit.alicaba.stockwise.R
import edu.cit.alicaba.stockwise.core.ApiClient
import edu.cit.alicaba.stockwise.core.ItemApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts


class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ItemApiService::class.java)

    private lateinit var tvSectionTitle: android.widget.TextView
    private lateinit var layoutEmptyState: android.widget.LinearLayout
    private var currentCategoryName = "All Products" // Tracks the title
    private var allItemsList: List<Item> = emptyList()
    private var isGridView = true // Defaults to Product Grid
    private var currentImageBase64: String? = null
    private var currentImageView: ImageView? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            currentImageView?.setImageURI(it)
            val bytes = contentResolver.openInputStream(it)?.readBytes()
            currentImageBase64 = bytes?.let { b -> Base64.encodeToString(b, Base64.NO_WRAP) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerViewInventory)
        tvSectionTitle = findViewById(R.id.tvSectionTitle)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)

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
                    currentCategoryName = "All Products"
                    refreshUI(allItemsList)
                }
                R.id.nav_quick_inventory -> {
                    isGridView = false
                    currentCategoryName = "Quick Inventory"
                    refreshUI(allItemsList)
                }
                R.id.nav_all_products -> filterCategory("All Products")
                R.id.nav_beverages -> filterCategory("Beverages")
                R.id.nav_snacks -> filterCategory("Snacks")
                R.id.nav_canned_goods -> filterCategory("Canned Goods")
                R.id.nav_toiletries -> filterCategory("Toiletries")
                R.id.nav_others -> filterCategory("Others")

                // --- NEW: THE LOGOUT FUNCTION ---
                R.id.nav_logout -> {
                    // This clears the backstack so they can't hit the "Back" button to re-enter
                    val intent = android.content.Intent(this, edu.cit.alicaba.stockwise.auth.LoginActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
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

        // Wired to the NEW master product dialog (null means we are Adding)
        findViewById<ExtendedFloatingActionButton>(R.id.fabAddItem).setOnClickListener {
            showProductDialog(null)
        }

        fetchInventory()
    }

    // --- THE MAGIC ADAPTER SWAPPER ---
    // --- THE MAGIC ADAPTER SWAPPER & UI UPDATER ---
    private fun refreshUI(listToDisplay: List<Item>) {
        // 1. Update the Section Title text
        tvSectionTitle.text = currentCategoryName.uppercase()

        // 2. Toggle the Empty State visibility
        if (listToDisplay.isEmpty()) {
            recyclerView.visibility = android.view.View.GONE
            layoutEmptyState.visibility = android.view.View.VISIBLE
        } else {
            recyclerView.visibility = android.view.View.VISIBLE
            layoutEmptyState.visibility = android.view.View.GONE
        }

        // 3. Load the correct adapter
        if (isGridView) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.adapter = ProductGridAdapter(listToDisplay) { item -> showProductDialog(item) }
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = InventoryAdapter(listToDisplay,
                onStockChange = { item, newQty -> updateItemStock(item, newQty) },
                onItemLongClick = { item -> showDeleteConfirmation(item) }
            )
        }
    }

    private fun filterCategory(category: String) {
        currentCategoryName = category
        if (category == "All Products") {
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

    private fun updateItemStock(item: Item, newQuantity: Int) {
        val updatedItem = item.copy(quantity = newQuantity)
        // Added !! to item.id to ensure null safety
        apiService.updateItem(item.id!!, updatedItem).enqueue(object : Callback<Item> {
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
                // Added !! to item.id to ensure null safety
                apiService.deleteItem(item.id!!).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) fetchInventory()
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- THE MASTER PRODUCT FORM (Handles both Add and Edit) ---
    private fun showProductDialog(itemToEdit: Item? = null) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_product_form, null)

        // 1. Find all our input boxes
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etFormName)
        val etPrice = dialogView.findViewById<TextInputEditText>(R.id.etFormPrice)
        val etQty = dialogView.findViewById<TextInputEditText>(R.id.etFormQty)
        val ivProductImage = dialogView.findViewById<ImageView>(R.id.ivProductImagePicker)

        // 2. Setup the Dropdown Category Menu
        val etCategory = dialogView.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.etFormCategory)
        val categories = arrayOf("Beverages", "Snacks", "Canned Goods", "Toiletries", "Others")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        etCategory.setAdapter(adapter)

        // 3. Reset the image tracker for the new popup
        currentImageView = ivProductImage
        currentImageBase64 = itemToEdit?.imageBase64

        // 4. If editing and they already have an image, decode it and show it!
        if (currentImageBase64 != null) {
            try {
                val decodedString = Base64.decode(currentImageBase64, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                ivProductImage.setImageBitmap(decodedByte)
            } catch (e: Exception) { e.printStackTrace() }
        }

        // When they tap the image box, open the phone's gallery
        ivProductImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 5. If editing, pre-fill all the text boxes
        itemToEdit?.let {
            etName.setText(it.name)
            etCategory.setText(it.category, false) // false prevents the dropdown from popping open instantly

            // Clean up the price decimal for the edit box too!
            val formattedPrice = if (it.price % 1.0 == 0.0) it.price.toInt().toString() else it.price.toString()
            etPrice.setText(formattedPrice)

            etQty.setText(it.quantity.toString())
        }

        val title = if (itemToEdit == null) "Add New Product" else "Edit Product"

        // 6. Build the Dialog
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val category = etCategory.text.toString()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val qty = etQty.text.toString().toIntOrNull() ?: 0

                if (name.isEmpty() || category.isEmpty()) {
                    Toast.makeText(this, "Name and Category required!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (itemToEdit == null) {
                    // CREATE NEW ITEM
                    val newItem = Item(name = name, category = category, price = price, quantity = qty, imageBase64 = currentImageBase64)
                    apiService.createItem(newItem).enqueue(object : Callback<Item> {
                        override fun onResponse(call: Call<Item>, response: Response<Item>) {
                            if (response.isSuccessful) fetchInventory()
                        }
                        override fun onFailure(call: Call<Item>, t: Throwable) {}
                    })
                } else {
                    // UPDATE EXISTING ITEM
                    val updatedItem = itemToEdit.copy(name = name, category = category, price = price, quantity = qty, imageBase64 = currentImageBase64)
                    apiService.updateItem(itemToEdit.id!!, updatedItem).enqueue(object : Callback<Item> {
                        override fun onResponse(call: Call<Item>, response: Response<Item>) {
                            if (response.isSuccessful) fetchInventory()
                        }
                        override fun onFailure(call: Call<Item>, t: Throwable) {}
                    })
                }
            }
            .setNegativeButton("Cancel", null)

        // 7. Add Delete Button ONLY if we are editing an existing item!
        if (itemToEdit != null) {
            builder.setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmation(itemToEdit)
            }
        }

        builder.show()
    }
}