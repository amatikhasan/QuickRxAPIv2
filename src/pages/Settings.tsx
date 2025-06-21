import React, { useState, useEffect } from 'react'
import { Save, Settings as SettingsIcon } from 'lucide-react'
import { adminAPI } from '../services/api'
import toast from 'react-hot-toast'

interface AppInfo {
  id: string
  subscription_fee: string
  account_number: string
  hotline: string
  facebook_link: string
  about_us: string
  terms_and_condition: string
  created_at: string
  updated_at: string
}

export const Settings: React.FC = () => {
  const [appInfo, setAppInfo] = useState<AppInfo | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [formData, setFormData] = useState({
    subscription_fee: '',
    account_number: '',
    hotline: '',
    facebook_link: '',
    about_us: '',
    terms_and_condition: ''
  })

  useEffect(() => {
    fetchAppInfo()
  }, [])

  const fetchAppInfo = async () => {
    try {
      const response = await adminAPI.getAppInfo()
      if (response.data) {
        setAppInfo(response.data)
        setFormData({
          subscription_fee: response.data.subscription_fee || '',
          account_number: response.data.account_number || '',
          hotline: response.data.hotline || '',
          facebook_link: response.data.facebook_link || '',
          about_us: response.data.about_us || '',
          terms_and_condition: response.data.terms_and_condition || ''
        })
      }
    } catch (error) {
      console.error('Error fetching app info:', error)
      toast.error('Failed to fetch app information')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSaving(true)

    const data = new FormData()
    data.append('id', appInfo?.id || '1')
    Object.entries(formData).forEach(([key, value]) => {
      data.append(key, value)
    })

    try {
      await adminAPI.updateAppInfo(data)
      toast.success('Settings updated successfully')
      fetchAppInfo()
    } catch (error) {
      console.error('Error updating settings:', error)
      toast.error('Failed to update settings')
    } finally {
      setIsSaving(false)
    }
  }

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }))
  }

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
        <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
        <div className="flex items-center space-x-2 text-sm text-gray-500">
          <SettingsIcon className="h-4 w-4" />
          <span>Application Configuration</span>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Basic Information */}
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Basic Information</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Subscription Fee
              </label>
              <input
                type="number"
                step="0.01"
                className="input-field"
                value={formData.subscription_fee}
                onChange={(e) => handleInputChange('subscription_fee', e.target.value)}
                placeholder="Enter subscription fee"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Account Number
              </label>
              <input
                type="text"
                className="input-field"
                value={formData.account_number}
                onChange={(e) => handleInputChange('account_number', e.target.value)}
                placeholder="Enter account number"
              />
            </div>
          </div>
        </div>

        {/* Contact Information */}
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Contact Information</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hotline Number
              </label>
              <input
                type="text"
                className="input-field"
                value={formData.hotline}
                onChange={(e) => handleInputChange('hotline', e.target.value)}
                placeholder="Enter hotline number"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Facebook Link
              </label>
              <input
                type="url"
                className="input-field"
                value={formData.facebook_link}
                onChange={(e) => handleInputChange('facebook_link', e.target.value)}
                placeholder="Enter Facebook page URL"
              />
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Content</h3>
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                About Us
              </label>
              <textarea
                rows={6}
                className="input-field"
                value={formData.about_us}
                onChange={(e) => handleInputChange('about_us', e.target.value)}
                placeholder="Enter about us content"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Terms and Conditions
              </label>
              <textarea
                rows={8}
                className="input-field"
                value={formData.terms_and_condition}
                onChange={(e) => handleInputChange('terms_and_condition', e.target.value)}
                placeholder="Enter terms and conditions"
              />
            </div>
          </div>
        </div>

        {/* Save Button */}
        <div className="flex justify-end">
          <button
            type="submit"
            disabled={isSaving}
            className="btn-primary flex items-center space-x-2"
          >
            {isSaving ? (
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
            ) : (
              <Save className="h-4 w-4" />
            )}
            <span>{isSaving ? 'Saving...' : 'Save Settings'}</span>
          </button>
        </div>
      </form>

      {/* Last Updated */}
      {appInfo && (
        <div className="text-sm text-gray-500 text-center">
          Last updated: {appInfo.updated_at}
        </div>
      )}
    </div>
  )
}