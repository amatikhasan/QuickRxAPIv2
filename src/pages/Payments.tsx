import React, { useState, useEffect } from 'react'
import { Search, CreditCard, DollarSign } from 'lucide-react'
import { adminAPI } from '../services/api'
import toast from 'react-hot-toast'

interface Payment {
  id: string
  user_id: string
  user_name: string
  amount: string
  method: string
  account_number: string
  trx_id: string
  created_at: string
  updated_at: string
}

export const Payments: React.FC = () => {
  const [payments, setPayments] = useState<Payment[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')

  useEffect(() => {
    fetchPayments()
  }, [])

  const fetchPayments = async () => {
    try {
      const response = await adminAPI.getPayments()
      if (Array.isArray(response.data)) {
        setPayments(response.data)
      }
    } catch (error) {
      console.error('Error fetching payments:', error)
      toast.error('Failed to fetch payments')
    } finally {
      setIsLoading(false)
    }
  }

  const filteredPayments = payments.filter(payment =>
    payment.user_name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    payment.method?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    payment.trx_id?.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const totalAmount = payments.reduce((sum, payment) => sum + parseFloat(payment.amount || '0'), 0)

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
        <h1 className="text-2xl font-bold text-gray-900">Payments</h1>
        <div className="flex items-center space-x-2 text-sm text-gray-500">
          <DollarSign className="h-4 w-4" />
          <span>Total: ${totalAmount.toFixed(2)}</span>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
        <div className="bg-green-50 overflow-hidden rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="bg-green-500 p-3 rounded-lg">
                  <DollarSign className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-green-600 truncate">
                    Total Revenue
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900">
                    ${totalAmount.toFixed(2)}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-blue-50 overflow-hidden rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="bg-blue-500 p-3 rounded-lg">
                  <CreditCard className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-blue-600 truncate">
                    Total Payments
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900">
                    {payments.length}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-purple-50 overflow-hidden rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="bg-purple-500 p-3 rounded-lg">
                  <DollarSign className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-purple-600 truncate">
                    Average Payment
                  </dt>
                  <dd className="text-2xl font-bold text-gray-900">
                    ${payments.length > 0 ? (totalAmount / payments.length).toFixed(2) : '0.00'}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
        <input
          type="text"
          placeholder="Search payments by user, method, or transaction ID..."
          className="input-field pl-10"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* Payments Table */}
      <div className="card">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Amount
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Method
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Account Number
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Transaction ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredPayments.map((payment) => (
                <tr key={payment.id} className="table-row">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="h-10 w-10 rounded-full bg-green-100 flex items-center justify-center">
                        <CreditCard className="h-5 w-5 text-green-600" />
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {payment.user_name || 'Unknown User'}
                        </div>
                        <div className="text-sm text-gray-500">ID: {payment.user_id}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">
                      ${parseFloat(payment.amount || '0').toFixed(2)}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {payment.method || 'Unknown'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {payment.account_number || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {payment.trx_id || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {payment.created_at}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}