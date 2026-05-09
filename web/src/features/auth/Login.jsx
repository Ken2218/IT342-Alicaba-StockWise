import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(''); // Added success state
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(''); // Clear previous success messages

    if (!formData.email || !formData.password) {
      setError('Email/Name and password are required.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/login', formData);
      if (response.data.success) {
        // Save the token
        localStorage.setItem('token', response.data.data.accessToken);

        // Show success message and delay the redirect!
        setSuccess('Logging in successfully! Redirecting to dashboard...');
        setTimeout(() => {
          navigate('/dashboard');
        }, 2000); // 2000 milliseconds = 2 seconds delay
      }
    } catch (err) {
      if (err.response && err.response.status === 401) {
        setError('Invalid email/name or password.');
      } else {
        setError('An error occurred during login. Please try again.');
      }
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen px-4 bg-gray-50 sm:px-6 lg:px-8">
      {/* Main Container - Split Layout */}
      <div className="flex w-full max-w-4xl overflow-hidden bg-white shadow-2xl rounded-2xl">

        {/* Left Side - Soft Green Gradient (Hidden on small screens) */}
        <div className="hidden w-1/2 p-12 text-white bg-gradient-to-br from-emerald-500 to-emerald-900 md:flex md:flex-col md:justify-center md:items-start">
          <h1 className="mb-4 text-5xl font-extrabold tracking-tight">StockWise</h1>
          <p className="text-lg text-emerald-100">
            Streamline your inventory. Manage your store effortlessly.
          </p>
          <div className="mt-12 opacity-50">
            <svg className="w-24 h-24" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2L2 7l10 5 10-5-10-5zm0 7.5l-10-5v9.5l10 5 10-5V4.5l-10 5z"/>
            </svg>
          </div>
        </div>

        {/* Right Side - Form */}
        <div className="w-full px-8 py-12 md:w-1/2 lg:px-12">
          <h2 className="text-3xl font-bold text-gray-800">Welcome back</h2>
          <p className="mt-2 mb-8 text-sm text-gray-500">Please enter your details to sign in.</p>

          {/* Error and Success Banners */}
          {error && <div className="p-4 mb-6 text-sm text-red-700 bg-red-100 border-l-4 border-red-500 rounded">{error}</div>}
          {success && <div className="p-4 mb-6 text-sm text-green-700 bg-green-100 border-l-4 border-green-500 rounded">{success}</div>}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block mb-2 text-sm font-semibold text-gray-700">Email or Full Name</label>
              <input
                type="text"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-3 text-gray-900 transition duration-200 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white"
                placeholder="admin@stockwise.com or Kenneth"
              />
            </div>
            <div>
              <label className="block mb-2 text-sm font-semibold text-gray-700">Password</label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                className="w-full px-4 py-3 text-gray-900 transition duration-200 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white"
                placeholder="••••••••"
              />
            </div>
            <button
              type="submit"
              className="w-full py-3 mt-4 font-bold text-white transition duration-200 bg-emerald-600 rounded-lg shadow-lg hover:bg-emerald-700 hover:shadow-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2"
            >
              Sign In
            </button>
          </form>

          <p className="mt-8 text-sm text-center text-gray-600">
            Don't have an account? <Link to="/register" className="font-semibold text-emerald-600 transition hover:text-emerald-800 hover:underline">Create one</Link>
          </p>
        </div>

      </div>
    </div>
  );
};

export default Login;