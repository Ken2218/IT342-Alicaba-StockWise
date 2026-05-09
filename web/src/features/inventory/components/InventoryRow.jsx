import React from 'react';
import axios from 'axios';

const InventoryRow = ({ item, onUpdate }) => {

  const updateStock = async (newQuantity) => {
    if (newQuantity < 0) return; // Prevent negative stock
    try {
      // Send the update directly to your existing PUT endpoint
      await axios.put(`http://localhost:8080/api/v1/items/${item.id}`, {
        ...item,
        quantity: newQuantity
      });
      onUpdate(); // Refresh the list
    } catch (error) {
      console.error("Failed to update stock", error);
    }
  };

  return (
    <div className="flex items-center justify-between p-4 mb-3 bg-white border border-gray-100 shadow-sm rounded-xl">
      <div className="flex items-center gap-4">
        {item.imageBase64 && (
          <img src={item.imageBase64} alt={item.name} className="w-12 h-12 rounded-lg object-cover" />
        )}
        <div>
          <h4 className="font-bold text-gray-800">{item.name}</h4>
          <p className="text-xs text-gray-500 uppercase">{item.category}</p>
        </div>
      </div>

      {/* --- ARROW BUTTONS UI --- */}
      <div className="flex items-center gap-4 bg-gray-50 p-2 rounded-lg border">
        <button
          onClick={() => updateStock(item.quantity - 1)}
          className="w-8 h-8 flex items-center justify-center bg-white border rounded-md shadow-sm hover:bg-red-50 hover:text-red-600 transition-colors"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="3" d="M15 19l-7-7 7-7" />
          </svg>
        </button>

        <div className="flex flex-col items-center min-w-[80px]">
          <span className="text-lg font-black text-emerald-700">{item.quantity}</span>
          <span className="text-[10px] font-bold text-gray-400 uppercase tracking-tighter">left</span>
        </div>

        <button
          onClick={() => updateStock(item.quantity + 1)}
          className="w-8 h-8 flex items-center justify-center bg-white border rounded-md shadow-sm hover:bg-emerald-50 hover:text-emerald-600 transition-colors"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="3" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </div>
  );
};

export default InventoryRow;