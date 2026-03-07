import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const Register = () => {
  const [formData, setFormData] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.name || !formData.email || !formData.password) {
      setError('All fields are required.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/register', formData);
      if (response.data.success) {
        setSuccess('Registration successful! Redirecting to login...');
        setTimeout(() => navigate('/login'), 2000);
      }
    } catch (err) {
      if (err.response && err.response.data && err.response.data.error) {
        setError(err.response.data.error.message);
      } else {
        setError('Registration failed. Please try again.');
      }
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen px-4 bg-gray-50 sm:px-6 lg:px-8">
      {/* Main Container - Split Layout */}
      <div className="flex flex-row-reverse w-full max-w-4xl overflow-hidden bg-white shadow-2xl rounded-2xl">

        {/* Right Side - Soft Green Gradient (Hidden on small screens) */}
        <div className="hidden w-1/2 p-12 text-white bg-gradient-to-bl from-emerald-900 to-emerald-500 md:flex md:flex-col md:justify-center md:items-start">
          <h2 className="mb-4 text-4xl font-bold">Join StockWise</h2>
          <p className="text-emerald-100">
            Take control of your inventory today. Fast, secure, and built for your business.
          </p>
        </div>

        {/* Left Side - Form */}
        <div className="w-full px-8 py-12 md:w-1/2 lg:px-12">
          <h2 className="text-3xl font-bold text-gray-800">Create an Account</h2>
          <p className="mt-2 mb-8 text-sm text-gray-500">Fill in your details to get started.</p>

          {error && <div className="p-4 mb-6 text-sm text-red-700 bg-red-100 border-l-4 border-red-500 rounded">{error}</div>}
          {success && <div className="p-4 mb-6 text-sm text-green-700 bg-green-100 border-l-4 border-green-500 rounded">{success}</div>}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block mb-2 text-sm font-semibold text-gray-700">Full Name</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="w-full px-4 py-3 text-gray-900 transition duration-200 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white"
                placeholder="John Doe"
              />
            </div>
            <div>
              <label className="block mb-2 text-sm font-semibold text-gray-700">Email Address</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-3 text-gray-900 transition duration-200 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white"
                placeholder="john@example.com"
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
              className="w-full py-3 mt-6 font-bold text-white transition duration-200 bg-emerald-600 rounded-lg shadow-lg hover:bg-emerald-700 hover:shadow-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2"
            >
              Register Now
            </button>
          </form>

          <p className="mt-8 text-sm text-center text-gray-600">
            Already have an account? <Link to="/login" className="font-semibold text-emerald-600 transition hover:text-emerald-800 hover:underline">Sign in</Link>
          </p>
        </div>

      </div>
    </div>
  );
};

export default Register;