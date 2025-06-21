import React, { useState, useEffect } from 'react'
import { Users, Stethoscope, FolderTree, CreditCard, MessageSquare, TrendingUp } from 'lucide-react'
import { adminAPI } from '../services/api'

interface Stats {
  users: number
  diseases: number
  categories: number
  payments: number
  feedback: number
}

export const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<Stats>({
    users: 0,
    diseases: 0,
    categories: 0,
    payments: 0,
    feedback: 0
  })
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    fetchStats()
  }, [])

  const fetchStats = async () => {
    try {
      const [usersRes, diseasesRes, categoriesRes, paymentsRes, feedbackRes] = await Promise.all([
        adminAPI.getUsers(),
        adminAPI.getDiseases(),
        adminAPI.getCategories(),
        adminAPI.getPayments(),
        adminAPI.getFeedback()
      ])

      setStats({
        users: Array.isArray(usersRes.data) ? usersRes.data.length : 0,
        diseases: Array.isArray(diseasesRes.data) ? diseasesRes.data.length : 0,
        categories: Array.isArray(categoriesRes.data) ? categoriesRes.data.length : 0,
        payments: Array.isArray(paymentsRes.data) ? paymentsRes.data.length : 0,
        feedback: Array.isArray(feedbackRes.data) ? feedbackRes.data.length : 0
      })
    } catch (error) {
      console.error('Error fetching stats:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const statCards = [
    {
      name: 'Total Users',
      value: stats.users,
      icon: Users,
      color: 'bg-blue-500',
      bgColor: 'bg-blue-50',
      textColor: 'text-blue-600'
    },
    {
      name: 'Diseases',
      value: stats.diseases,
      icon: Stethoscope,
      color: 'bg-green-500',
      bgColor: 'bg-green-50',
      textColor: 'text-green-600'
    },
    {
      name: 'Categories',
      value: stats.categories,
      icon: FolderTree,
      color: 'bg-purple-500',
      bgColor: 'bg-purple-50',
      textColor: 'text-purple-600'
    },
    {
      name: 'Payments',
      value: stats.payments,
      icon: CreditCard,
      color: 'bg-yellow-500',
      bgColor: 'bg-yellow-50',
      textColor: 'text-yellow-600'
    },
    {
      name: 'Feedback',
      value: stats.feedback,
      icon: MessageSquare,
      color: 'bg-red-500',
      bgColor: 'bg-red-50',
      textColor: 'text-red-600'
    }
  ]

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <div className="flex items-center space-x-2 text-sm text-gray-500">
          <TrendingUp className="h-4 w-4" />
          <span>Last updated: {new Date().toLocaleDateString()}</span>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-5">
        {statCards.map((stat) => (
          <div key={stat.name} className={`${stat.bgColor} overflow-hidden rounded-lg`}>
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className={`${stat.color} p-3 rounded-lg`}>
                    <stat.icon className="h-6 w-6 text-white" />
                  </div>
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className={`text-sm font-medium ${stat.textColor} truncate`}>
                      {stat.name}
                    </dt>
                    <dd className="text-2xl font-bold text-gray-900">
                      {stat.value.toLocaleString()}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
          <div className="space-y-3">
            <button className="w-full text-left p-3 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors duration-150">
              <div className="flex items-center space-x-3">
                <FolderTree className="h-5 w-5 text-purple-600" />
                <span className="font-medium">Add New Category</span>
              </div>
            </button>
            <button className="w-full text-left p-3 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors duration-150">
              <div className="flex items-center space-x-3">
                <Stethoscope className="h-5 w-5 text-green-600" />
                <span className="font-medium">Add New Disease</span>
              </div>
            </button>
            <button className="w-full text-left p-3 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors duration-150">
              <div className="flex items-center space-x-3">
                <Users className="h-5 w-5 text-blue-600" />
                <span className="font-medium">Manage Users</span>
              </div>
            </button>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Activity</h3>
          <div className="space-y-3">
            <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
              <div className="w-2 h-2 bg-green-500 rounded-full"></div>
              <span className="text-sm text-gray-600">New user registered</span>
              <span className="text-xs text-gray-400 ml-auto">2 min ago</span>
            </div>
            <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
              <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
              <span className="text-sm text-gray-600">Payment received</span>
              <span className="text-xs text-gray-400 ml-auto">5 min ago</span>
            </div>
            <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
              <div className="w-2 h-2 bg-purple-500 rounded-full"></div>
              <span className="text-sm text-gray-600">New feedback submitted</span>
              <span className="text-xs text-gray-400 ml-auto">10 min ago</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}