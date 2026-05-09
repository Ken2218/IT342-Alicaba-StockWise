import React from 'react';

const ProductCard = ({ item, onEdit }) => {
  // You can change this number to whatever "low stock" means for your store
  const LOW_STOCK_THRESHOLD = 5;
  const isLowStock = item.quantity <= LOW_STOCK_THRESHOLD;

  return (
    <div className={`flex flex-col overflow-hidden transition-all duration-300 bg-white border shadow-sm rounded-2xl hover:shadow-xl hover:-translate-y-1 group
      ${isLowStock
        ? 'border-yellow-400 ring-4 ring-yellow-400/30 ring-inset'
        : 'border-gray-200'
      }`}>

      {/* Image Container */}
      <div className="relative flex items-center justify-center w-full p-5 bg-white border-b border-gray-100 h-48">
        {item.imageBase64 ? (
          <img
            src={item.imageBase64}
            alt={item.name}
            className="object-contain w-full h-full transition-transform duration-300 group-hover:scale-105"
          />
        ) : (
          <div className="flex flex-col items-center justify-center w-full h-full text-gray-400 bg-gray-50 rounded-xl">
            <span className="text-sm font-medium">No Image</span>
          </div>
        )}
      </div>

      {/* Product Details */}
      <div className="flex flex-col flex-grow p-5 bg-white">
        <div>
          <h3 className="mb-1 text-lg font-bold text-gray-900 capitalize line-clamp-2">
            {item.name}
          </h3>
          <p className="text-xs font-medium tracking-wide text-gray-500 uppercase">
            {item.category}
          </p>
        </div>

        {/* Price and Stock Badge */}
        <div className="flex flex-wrap items-center justify-between gap-2 pt-4 mt-auto mb-4">
          <span className="text-2xl font-black text-emerald-700">
            ₱{item.price.toFixed(2)}
          </span>
          {/* Badge color also updates based on the same threshold */}
          <span className={`px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap shadow-sm ${
            isLowStock
              ? 'bg-yellow-100 text-yellow-700 border border-yellow-200'
              : 'bg-emerald-50 text-emerald-700 border border-emerald-200'
          }`}>
            {item.quantity} in stock
          </span>
        </div>

        {/* Edit Action Button */}
        <button
          onClick={() => onEdit(item)}
          className="w-full py-2.5 text-sm font-bold text-emerald-700 transition-all border border-emerald-200 bg-emerald-50 rounded-xl hover:bg-emerald-600 hover:text-white"
        >
          Edit Product
        </button>
      </div>
    </div>
  );
};

export default ProductCard;