import React, { useState, useEffect } from 'react'
import { Plus, Edit, Trash2, Search, Stethoscope } from 'lucide-react'
import { adminAPI } from '../services/api'
import toast from 'react-hot-toast'

interface Disease {
  id: string
  name: string
  cat_id: string
  clue_to_dx: string
  advice: string
  treatment: string
  details: string
  created_at: string
  updated_at: string
}

interface Category {
  id: string
  name: string
  type: string
}

export const Diseases: React.FC = () => {
  const [diseases, setDiseases] = useState<Disease[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [editingDisease, setEditingDisease] = useState<Disease | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    cat_id: '',
    clue_to_dx: '',
    advice: '',
    treatment: '',
    details: ''
  })

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [diseasesRes, categoriesRes] = await Promise.all([
        adminAPI.getDiseases(),
        adminAPI.getCategories()
      ])
      
      if (Array.isArray(diseasesRes.data)) {
        setDiseases(diseasesRes.data)
      }
      if (Array.isArray(categoriesRes.data)) {
        setCategories(categoriesRes.data)
      }
    } catch (error) {
      console.error('Error fetching data:', error)
      toast.error('Failed to fetch data')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    const data = new FormData()
    Object.entries(formData).forEach(([key, value]) => {
      data.append(key, value)
    })

    try {
      if (editingDisease) {
        data.append('id', editingDisease.id)
        await adminAPI.updateDisease(data)
        toast.success('Disease updated successfully')
      } else {
        await adminAPI.createDisease(data)
        toast.success('Disease created successfully')
      }
      
      setShowModal(false)
      setEditingDisease(null)
      setFormData({
        name: '',
        cat_id: '',
        clue_to_dx: '',
        advice: '',
        treatment: '',
        details: ''
      })
      fetchData()
    } catch (error) {
      console.error('Error saving disease:', error)
      toast.error('Failed to save disease')
    }
  }

  const handleDelete = async (disease: Disease) => {
    if (!confirm('Are you sure you want to delete this disease?')) return

    const data = new FormData()
    data.append('id', disease.id)

    try {
      await adminAPI.deleteDisease(data)
      toast.success('Disease deleted successfully')
      fetchData()
    } catch (error) {
      console.error('Error deleting disease:', error)
      toast.error('Failed to delete disease')
    }
  }

  const handleEdit = (disease: Disease) => {
    setEditingDisease(disease)
    setFormData({
      name: disease.name,
      cat_id: disease.cat_id,
      clue_to_dx: disease.clue_to_dx || '',
      advice: disease.advice || '',
      treatment: disease.treatment || '',
      details: disease.details || ''
    })
    setShowModal(true)
  }

  const filteredDiseases = diseases.filter(disease =>
    disease.name.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const getCategoryName = (catId: string) => {
    const category = categories.find(cat => cat.id === catId)
    return category ? category.name : 'Unknown'
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
        <h1 className="text-2xl font-bold text-gray-900">Diseases</h1>
        <button
          onClick={() => setShowModal(true)}
          className="btn-primary flex items-center space-x-2"
        >
          <Plus className="h-4 w-4" />
          <span>Add Disease</span>
        </button>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
        <input
          type="text"
          placeholder="Search diseases..."
          className="input-field pl-10"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* Diseases Table */}
      <div className="card">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Disease
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Clue to Diagnosis
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Created
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredDiseases.map((disease) => (
                <tr key={disease.id} className="table-row">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="h-10 w-10 rounded-lg bg-medical-100 flex items-center justify-center">
                        <Stethoscope className="h-5 w-5 text-medical-600" />
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">{disease.name}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {getCategoryName(disease.cat_id)}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm text-gray-900 max-w-xs truncate">
                      {disease.clue_to_dx || 'No clues provided'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {disease.created_at}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex space-x-2">
                      <button
                        onClick={() => handleEdit(disease)}
                        className="text-primary-600 hover:text-primary-900"
                      >
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(disease)}
                        className="text-red-600 hover:text-red-900"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-10 mx-auto p-5 border w-full max-w-2xl shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                {editingDisease ? 'Edit Disease' : 'Add New Disease'}
              </h3>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Disease Name
                    </label>
                    <input
                      type="text"
                      required
                      className="input-field"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Category
                    </label>
                    <select
                      required
                      className="input-field"
                      value={formData.cat_id}
                      onChange={(e) => setFormData({ ...formData, cat_id: e.target.value })}
                    >
                      <option value="">Select Category</option>
                      {categories.map((cat) => (
                        <option key={cat.id} value={cat.id}>
                          {cat.name}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Clue to Diagnosis
                  </label>
                  <textarea
                    rows={3}
                    className="input-field"
                    value={formData.clue_to_dx}
                    onChange={(e) => setFormData({ ...formData, clue_to_dx: e.target.value })}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Advice
                  </label>
                  <textarea
                    rows={3}
                    className="input-field"
                    value={formData.advice}
                    onChange={(e) => setFormData({ ...formData, advice: e.target.value })}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Treatment
                  </label>
                  <textarea
                    rows={3}
                    className="input-field"
                    value={formData.treatment}
                    onChange={(e) => setFormData({ ...formData, treatment: e.target.value })}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Details
                  </label>
                  <textarea
                    rows={4}
                    className="input-field"
                    value={formData.details}
                    onChange={(e) => setFormData({ ...formData, details: e.target.value })}
                  />
                </div>

                <div className="flex justify-end space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={() => {
                      setShowModal(false)
                      setEditingDisease(null)
                      setFormData({
                        name: '',
                        cat_id: '',
                        clue_to_dx: '',
                        advice: '',
                        treatment: '',
                        details: ''
                      })
                    }}
                    className="btn-secondary"
                  >
                    Cancel
                  </button>
                  <button type="submit" className="btn-primary">
                    {editingDisease ? 'Update' : 'Create'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}