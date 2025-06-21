import React, { useState, useEffect } from 'react'
import { Search, MessageSquare, User } from 'lucide-react'
import { adminAPI } from '../services/api'
import toast from 'react-hot-toast'

interface Feedback {
  id: string
  user_id: string
  user_name: string
  feedback: string
  created_at: string
  updated_at: string
}

export const Feedback: React.FC = () => {
  const [feedback, setFeedback] = useState<Feedback[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')

  useEffect(() => {
    fetchFeedback()
  }, [])

  const fetchFeedback = async () => {
    try {
      const response = await adminAPI.getFeedback()
      if (Array.isArray(response.data)) {
        setFeedback(response.data)
      }
    } catch (error) {
      console.error('Error fetching feedback:', error)
      toast.error('Failed to fetch feedback')
    } finally {
      setIsLoading(false)
    }
  }

  const filteredFeedback = feedback.filter(item =>
    item.user_name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    item.feedback?.toLowerCase().includes(searchTerm.toLowerCase())
  )

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
        <h1 className="text-2xl font-bold text-gray-900">Feedback</h1>
        <div className="text-sm text-gray-500">
          Total: {feedback.length} feedback entries
        </div>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
        <input
          type="text"
          placeholder="Search feedback by user or content..."
          className="input-field pl-10"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* Feedback Cards */}
      <div className="grid gap-6">
        {filteredFeedback.map((item) => (
          <div key={item.id} className="card">
            <div className="flex items-start space-x-4">
              <div className="flex-shrink-0">
                <div className="h-12 w-12 rounded-full bg-purple-100 flex items-center justify-center">
                  <User className="h-6 w-6 text-purple-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-medium text-gray-900">
                      {item.user_name || 'Anonymous User'}
                    </h3>
                    <p className="text-sm text-gray-500">User ID: {item.user_id}</p>
                  </div>
                  <div className="text-sm text-gray-500">
                    {item.created_at}
                  </div>
                </div>
                <div className="mt-4">
                  <div className="flex items-start space-x-2">
                    <MessageSquare className="h-5 w-5 text-gray-400 mt-0.5 flex-shrink-0" />
                    <p className="text-gray-700 leading-relaxed">{item.feedback}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredFeedback.length === 0 && (
        <div className="text-center py-12">
          <MessageSquare className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">No feedback found</h3>
          <p className="mt-1 text-sm text-gray-500">
            {searchTerm ? 'Try adjusting your search terms.' : 'No feedback has been submitted yet.'}
          </p>
        </div>
      )}
    </div>
  )
}