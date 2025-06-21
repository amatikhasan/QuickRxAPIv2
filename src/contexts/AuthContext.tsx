import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { api } from '../services/api'
import toast from 'react-hot-toast'

interface User {
  id: string
  username: string
  email: string
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (username: string, password: string) => Promise<boolean>
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

interface AuthProviderProps {
  children: ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem('admin_token')
    const storedUser = localStorage.getItem('admin_user')
    
    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
      api.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`
    }
    
    setIsLoading(false)
  }, [])

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const formData = new FormData()
      formData.append('username', username)
      formData.append('password', password)

      const response = await api.post('/api/v2/Admin.php?call=login', formData)
      
      if (response.data.error === false) {
        const authToken = response.data.response
        const userData = { id: '1', username, email: `${username}@quickrx.com` }
        
        setToken(authToken)
        setUser(userData)
        
        localStorage.setItem('admin_token', authToken)
        localStorage.setItem('admin_user', JSON.stringify(userData))
        
        api.defaults.headers.common['Authorization'] = `Bearer ${authToken}`
        
        toast.success('Login successful!')
        return true
      } else {
        toast.error(response.data.response || 'Login failed')
        return false
      }
    } catch (error) {
      console.error('Login error:', error)
      toast.error('Login failed. Please check your credentials.')
      return false
    }
  }

  const logout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    delete api.defaults.headers.common['Authorization']
    toast.success('Logged out successfully')
  }

  const value = {
    user,
    token,
    login,
    logout,
    isLoading
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}