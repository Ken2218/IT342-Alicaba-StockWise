import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Login';
import Register from './Register';

// A simple placeholder for the dashboard to prove your login worked!
const Dashboard = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-green-50">
      <h1 className="text-4xl font-bold text-green-700">Welcome to StockWise!</h1>
      <p className="mt-4 text-lg text-gray-700">You have successfully logged in.</p>
      <button 
        onClick={() => {
          localStorage.removeItem('token');
          window.location.href = '/login';
        }}
        className="px-4 py-2 mt-8 font-bold text-white bg-red-500 rounded hover:bg-red-600"
      >
        Log Out
      </button>
    </div>
  );
};

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Default route redirects immediately to the Login page */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          
          {/* Authentication Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* The Dashboard Route (Where users go after logging in) */}
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;