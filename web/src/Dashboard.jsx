import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import AddProductModal from './AddProductModal';

const Dashboard = () => {
  const [items, setItems] = useState([]);
  const [globalMessage, setGlobalMessage] = useState({ type: '', text: '' });
  const [isModalOpen, setIsModalOpen] = useState(false);
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

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/v1/items/${id}`);
      setGlobalMessage({ type: 'success', text: 'Item successfully deleted.' });
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

  return (
    <div className="min-h-screen p-8 bg-gray-50 relative">
      <div className="max-w-6xl mx-auto pb-24"> {/* Added padding bottom so the table doesn't hide behind the button */}

        {/* Header section */}
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-3xl font-bold text-emerald-800">StockWise Inventory</h1>
          <button onClick={handleLogout} className="px-4 py-2 font-semibold text-red-600 transition bg-red-100 rounded-lg hover:bg-red-200">
            Logout
          </button>
        </div>

        {/* Global Success/Error Banner */}
        {globalMessage.text && (
          <div className={`p-4 mb-6 text-sm rounded-lg ${globalMessage.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-emerald-100 text-emerald-700'}`}>
            {globalMessage.text}
          </div>
        )}

        {/* Controls Section (Old button removed from here) */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-gray-800">Current Stock Levels</h2>
        </div>

        {/* Inventory Table */}
        <div className="p-6 bg-white shadow-lg rounded-xl">
          <div className="overflow-x-auto">
            <table className="min-w-full text-left bg-white border-collapse">
               <thead>
                 <tr className="border-b-2 border-emerald-100">
                   <th className="py-3 font-semibold text-gray-600">Image</th>
                   <th className="py-3 font-semibold text-gray-600">Name</th>
                   <th className="py-3 font-semibold text-gray-600">Qty</th>
                   <th className="py-3 font-semibold text-gray-600">Price</th>
                   <th className="py-3 font-semibold text-center text-gray-600">Actions</th>
                 </tr>
               </thead>
               <tbody>
                 {items.length === 0 ? (
                   <tr><td colSpan="5" className="py-6 text-center text-gray-500">No products in inventory. Click the floating button to get started!</td></tr>
                 ) : (
                   items.map((item) => (
                     <tr key={item.id} className="border-b hover:bg-gray-50">
                       <td className="py-3">
                         {item.imageBase64 ? (
                           <img src={item.imageBase64} alt={item.name} className="object-cover w-12 h-12 rounded-md shadow-sm" />
                         ) : (
                           <div className="flex items-center justify-center w-12 h-12 text-xs text-gray-400 bg-gray-100 rounded-md">No Img</div>
                         )}
                       </td>
                       <td className="py-3 font-medium text-gray-800">{item.name}</td>
                       <td className="py-3 text-gray-800">
                         <span className={`px-2 py-1 text-xs font-bold rounded-full ${item.quantity < 10 ? 'bg-red-100 text-red-700' : 'bg-emerald-100 text-emerald-700'}`}>
                           {item.quantity}
                         </span>
                       </td>
                       <td className="py-3 text-gray-800">₱{item.price.toFixed(2)}</td>
                       <td className="py-3 text-center">
                         <button onClick={() => handleDelete(item.id)} className="text-sm font-semibold text-red-500 hover:text-red-700">Delete</button>
                       </td>
                     </tr>
                   ))
                 )}
               </tbody>
             </table>
          </div>
        </div>

      </div>

      {/* --- NEW FLOATING ACTION BUTTON (FAB) --- */}
      <button
        onClick={() => {
          setGlobalMessage({ type: '', text: '' });
          setIsModalOpen(true);
        }}
        className="fixed z-40 flex items-center px-6 py-4 font-bold text-white transition-all duration-300 transform shadow-2xl bottom-10 right-10 bg-emerald-600 rounded-full hover:bg-emerald-700 hover:scale-105 hover:shadow-emerald-500/50 focus:outline-none focus:ring-4 focus:ring-emerald-300"
      >
        <svg className="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path>
        </svg>
        Add Product
      </button>

      {/* Add Product Modal Component */}
      <AddProductModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={handleModalSuccess}
      />

    </div>
  );
};

export default Dashboard;