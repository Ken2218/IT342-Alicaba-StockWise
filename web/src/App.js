import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// UPDATED IMPORTS: Pointing to the new Vertical Slice folders
import Login from './features/auth/Login';
import Register from './features/auth/Register';
import Dashboard from './features/inventory/Dashboard';

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