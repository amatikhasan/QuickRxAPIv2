import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from './contexts/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Layout } from './components/Layout'
import { Login } from './pages/Login'
import { Dashboard } from './pages/Dashboard'
import { Categories } from './pages/Categories'
import { Diseases } from './pages/Diseases'
import { Users } from './pages/Users'
import { Payments } from './pages/Payments'
import { Feedback } from './pages/Feedback'
import { Settings } from './pages/Settings'

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/" element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }>
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="categories" element={<Categories />} />
              <Route path="diseases" element={<Diseases />} />
              <Route path="users" element={<Users />} />
              <Route path="payments" element={<Payments />} />
              <Route path="feedback" element={<Feedback />} />
              <Route path="settings" element={<Settings />} />
            </Route>
          </Routes>
          <Toaster position="top-right" />
        </div>
      </Router>
    </AuthProvider>
  )
}

export default App