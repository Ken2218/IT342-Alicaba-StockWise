import React, { useState } from 'react';
import axios from 'axios';

const AddProductModal = ({ isOpen, onClose, onSuccess }) => {
  // Swapped 'sku' for 'imageBase64'
  const [formData, setFormData] = useState({ imageBase64: '', name: '', quantity: '', price: '' });
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Convert uploaded image to Base64 format so Spring Boot can save it easily
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData({ ...formData, imageBase64: reader.result });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Require all fields except image (make image optional to start)
    if (!formData.name || !formData.quantity || !formData.price) {
      setError('Product Name, Quantity, and Price are required.');
      return;
    }

    try {
      await axios.post('http://localhost:8080/api/v1/items', formData);
      setFormData({ imageBase64: '', name: '', quantity: '', price: '' });
      onSuccess('Product successfully added!');
    } catch (err) {
      setError(err.response?.data || 'Failed to add item.');
    }
  };

  const handleCancel = () => {
    setError('');
    setFormData({ imageBase64: '', name: '', quantity: '', price: '' });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
      <div className="w-full max-w-md p-8 bg-white shadow-2xl rounded-2xl">
        <h2 className="mb-6 text-2xl font-bold text-gray-800">Add New Product</h2>

        {error && (
          <div className="p-3 mb-4 text-sm text-red-700 bg-red-100 rounded-lg">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">

          {/* 1. Image Upload Section */}
          <div>
            <label className="block mb-1 text-sm font-semibold text-gray-700">Product Image</label>
            <div className="flex items-center justify-center w-full">
              <label className="flex flex-col items-center justify-center w-full h-32 transition border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 hover:bg-gray-100">
                <div className="flex flex-col items-center justify-center pt-5 pb-6 overflow-hidden">
                  {formData.imageBase64 ? (
                    <img src={formData.imageBase64} alt="Preview" className="object-contain h-28 rounded-md" />
                  ) : (
                    <>
                      <svg className="w-8 h-8 mb-2 text-emerald-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                        <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
                      </svg>
                      <p className="text-sm text-gray-500"><span className="font-semibold">Click to upload</span> image</p>
                    </>
                  )}
                </div>
                <input type="file" className="hidden" accept="image/*" onChange={handleImageChange} />
              </label>
            </div>
          </div>

          {/* 2. Product Name */}
          <div>
            <label className="block mb-1 text-sm font-semibold text-gray-700">Product Name</label>
            <input type="text" name="name" value={formData.name} onChange={handleChange} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-emerald-500" placeholder="e.g. Coca-Cola 1L" />
          </div>

          {/* 3. Quantity & Price (Side-by-side format kept) */}
          <div className="flex gap-4">
            <div className="w-1/2">
              <label className="block mb-1 text-sm font-semibold text-gray-700">Quantity</label>
              <input type="number" name="quantity" value={formData.quantity} onChange={handleChange} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-emerald-500" placeholder="0" />
            </div>
            <div className="w-1/2">
              <label className="block mb-1 text-sm font-semibold text-gray-700">Price (₱)</label>
              <input type="number" step="0.01" name="price" value={formData.price} onChange={handleChange} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-emerald-500" placeholder="0.00" />
            </div>
          </div>

          <div className="flex justify-end gap-3 mt-8">
            <button
              type="button"
              onClick={handleCancel}
              className="px-5 py-2 font-semibold text-gray-600 transition bg-gray-100 rounded-lg hover:bg-gray-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 font-bold text-white transition bg-emerald-600 rounded-lg hover:bg-emerald-700"
            >
              Save Product
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddProductModal;