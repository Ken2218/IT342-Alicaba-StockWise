import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import ProductFormModal from './ProductFormModal';
import ProductCard from './ProductCard';
import InventoryRow from './InventoryRow'; // Ensure you have created this component

const SIDEBAR_CATEGORIES = ['All Products', 'Beverages', 'Snacks', 'Canned Goods', 'Toiletries', 'Others'];

const Dashboard = () => {
  const [items, setItems] = useState([]);
  const [view, setView] = useState('grid'); // Tracks if we are in 'grid' or 'inventory' view
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All Products');
  const [globalMessage, setGlobalMessage] = useState({ type: '', text: '' });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/v1/items');
      setItems(response.data);
    } catch (error) {
      console.error('Error fetching items:', error);
    }
  };

  const handleAddNewClick = () => {
    setGlobalMessage({ type: '', text: '' });
    setEditingItem(null);
    setIsModalOpen(true);
  };

  const handleEditClick = (item) => {
    setGlobalMessage({ type: '', text: '' });
    setEditingItem(item);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this item?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/v1/items/${id}`);
      setGlobalMessage({ type: 'success', text: 'Item successfully deleted.' });
      setIsModalOpen(false);
      fetchItems();
      setTimeout(() => setGlobalMessage({ type: '', text: '' }), 3000);
    } catch (error) {
      setGlobalMessage({ type: 'error', text: 'Failed to delete item.' });
    }
  };

  const handleModalSuccess = (successText) => {
    setIsModalOpen(false);
    fetchItems();
    setGlobalMessage({ type: 'success', text: successText });
    setTimeout(() => setGlobalMessage({ type: '', text: '' }), 3000);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const filteredItems = items.filter(item => {
    const matchesSearch = item.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'All Products' || item.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="relative min-h-screen bg-gray-100">

      {/* Top Navbar */}
      <div className="px-8 py-4 bg-white border-b shadow-sm sticky top-0 z-30">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <h1 className="text-2xl font-extrabold text-emerald-800 tracking-tight">StockWise</h1>
          <button onClick={handleLogout} className="px-4 py-2 text-sm font-semibold text-red-600 transition bg-red-50 rounded-lg hover:bg-red-100">
            Logout
          </button>
        </div>
      </div>

      <div className="flex max-w-7xl mx-auto mt-8 pb-24 px-8 gap-12">

        {/* --- LEFT SIDEBAR --- */}
        <div className="hidden w-64 shrink-0 md:block">
          <div className="p-5 bg-white shadow-sm rounded-2xl sticky top-24 border border-gray-200/50">

            {/* View Selection Menu */}
            <h3 className="px-3 mb-4 text-[10px] font-black tracking-[0.2em] text-gray-400 uppercase">Menu</h3>
            <ul className="space-y-1.5 mb-8">
              <li>
                <button
                  onClick={() => setView('grid')}
                  className={`w-full text-left px-4 py-2.5 rounded-xl text-sm font-bold transition-all ${
                    view === 'grid' ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-200' : 'text-gray-500 hover:bg-gray-50'
                  }`}
                >
                  Product Grid
                </button>
              </li>
              <li>
                <button
                  onClick={() => setView('inventory')}
                  className={`w-full text-left px-4 py-2.5 rounded-xl text-sm font-bold transition-all ${
                    view === 'inventory' ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-200' : 'text-gray-500 hover:bg-gray-50'
                  }`}
                >
                  Quick Inventory
                </button>
              </li>
            </ul>

            {/* Category Filter */}
            <h3 className="px-3 mb-4 text-[10px] font-black tracking-[0.2em] text-gray-400 uppercase">Categories</h3>
            <ul className="space-y-1">
              {SIDEBAR_CATEGORIES.map(category => (
                <li key={category}>
                  <button
                    onClick={() => { setSelectedCategory(category); setView('grid'); }}
                    className={`w-full text-left px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                      selectedCategory === category && view === 'grid'
                        ? 'bg-emerald-50 text-emerald-700'
                        : 'text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    {category}
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* --- MAIN CONTENT AREA --- */}
        <div className="flex-1 min-w-0">

          {/* Search Bar */}
          <div className="relative mb-8">
            <div className="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
              <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
            </div>
            <input
              type="text"
              className="w-full py-4 pl-12 pr-4 transition-all bg-white border border-gray-200 shadow-sm rounded-2xl focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Search products by name..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          {globalMessage.text && (
            <div className={`p-4 mb-6 text-sm rounded-xl shadow-sm ${globalMessage.type === 'error' ? 'bg-red-50 text-red-700 border border-red-100' : 'bg-emerald-50 text-emerald-700 border border-emerald-100'}`}>
              {globalMessage.text}
            </div>
          )}

          <h2 className="mb-6 text-xl font-black text-gray-800 tracking-tight uppercase">
            {view === 'grid' ? selectedCategory : 'Quick Inventory Management'}
          </h2>

          {/* Conditional Rendering of Views */}
          <div>
            {items.length === 0 ? (
              <div className="py-20 text-center bg-white border-2 border-gray-200 border-dashed rounded-3xl">
                <p className="text-gray-400 font-medium">No products in inventory.</p>
              </div>
            ) : filteredItems.length === 0 ? (
              <div className="py-20 text-center bg-white border border-gray-200 border-dashed rounded-3xl">
                <p className="text-gray-400 font-medium">No results match your search.</p>
              </div>
            ) : view === 'grid' ? (
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {filteredItems.map((item) => (
                  <ProductCard key={item.id} item={item} onEdit={handleEditClick} />
                ))}
              </div>
            ) : (
              <div className="max-w-2xl">
                {filteredItems.map((item) => (
                  <InventoryRow key={item.id} item={item} onUpdate={fetchItems} />
                ))}
              </div>
            )}
          </div>
        </div>

      </div>

      {/* Floating Add Button */}
      <button
        onClick={handleAddNewClick}
        className="fixed z-40 flex items-center px-6 py-4 font-bold text-white transition-all shadow-2xl bottom-10 right-10 bg-emerald-600 rounded-full hover:bg-emerald-700 hover:scale-105 active:scale-95"
      >
        <svg className="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path></svg>
        Add Product
      </button>

      <ProductFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={handleModalSuccess}
        onDelete={handleDelete}
        editingItem={editingItem}
      />
    </div>
  );
};

export default Dashboard;