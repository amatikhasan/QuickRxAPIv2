import React, { useState, useEffect } from 'react'
import { Plus, Edit, Trash2, Search, FolderTree } from 'lucide-react'
import { adminAPI } from '../services/api'
import toast from 'react-hot-toast'

interface Category {
  id: string
  name: string
  parent_cat_id: string
  type: string
  image_url: string
  created_at: string
  updated_at: string
}

export const Categories: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [editingCategory, setEditingCategory] = useState<Category | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    parent_cat_id: '0',
    type: '1',
    image: null as File | null
  })

  useEffect(() => {
    fetchCategories()
  }, [])

  const fetchCategories = async () => {
    try {
      const response = await adminAPI.getCategories()
      if (Array.isArray(response.data)) {
        setCategories(response.data)
      }
    } catch (error) {
      console.error('Error fetching categories:', error)
      toast.error('Failed to fetch categories')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    const data = new FormData()
    data.append('name', formData.name)
    data.append('parent_cat_id', formData.parent_cat_id)
    data.append('type', formData.type)
    
    if (formData.image) {
      data.append('file', formData.image)
    }

    try {
      if (editingCategory) {
        data.append('id', editingCategory.id)
        data.append('image_url', editingCategory.image_url)
        await adminAPI.updateCategory(data)
        toast.success('Category updated successfully')
      } else {
        await adminAPI.createCategory(data)
        toast.success('Category created successfully')
      }
      
      setShowModal(false)
      setEditingCategory(null)
      setFormData({ name: '', parent_cat_id: '0', type: '1', image: null })
      fetchCategories()
    } catch (error) {
      console.error('Error saving category:', error)
      toast.error('Failed to save category')
    }
  }

  const handleDelete = async (category: Category) => {
    if (!confirm('Are you sure you want to delete this category?')) return

    const data = new FormData()
    data.append('id', category.id)
    data.append('image_url', category.image_url)

    try {
      await adminAPI.deleteCategory(data)
      toast.success('Category deleted successfully')
      fetchCategories()
    } catch (error) {
      console.error('Error deleting category:', error)
      toast.error('Failed to delete category')
    }
  }

  const handleEdit = (category: Category) => {
    setEditingCategory(category)
    setFormData({
      name: category.name,
      parent_cat_id: category.parent_cat_id,
      type: category.type,
      image: null
    })
    setShowModal(true)
  }

  const filteredCategories = categories.filter(category =>
    category.name.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const mainCategories = categories.filter(cat => cat.type === '1')

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
        <h1 className="text-2xl font-bold text-gray-900">Categories</h1>
        <button
          onClick={() => setShowModal(true)}
          className="btn-primary flex items-center space-x-2"
        >
          <Plus className="h-4 w-4" />
          <span>Add Category</span>
        </button>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
        <input
          type="text"
          placeholder="Search categories..."
          className="input-field pl-10"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* Categories Table */}
      <div className="card">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Parent Category
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
              {filteredCategories.map((category) => (
                <tr key={category.id} className="table-row">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      {category.image_url ? (
                        <img
                          className="h-10 w-10 rounded-lg object-cover"
                          src={category.image_url}
                          alt={category.name}
                        />
                      ) : (
                        <div className="h-10 w-10 rounded-lg bg-gray-200 flex items-center justify-center">
                          <FolderTree className="h-5 w-5 text-gray-400" />
                        </div>
                      )}
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">{category.name}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      category.type === '1' 
                        ? 'bg-blue-100 text-blue-800' 
                        : 'bg-green-100 text-green-800'
                    }`}>
                      {category.type === '1' ? 'Main' : 'Sub'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {category.parent_cat_id === '0' 
                      ? 'None' 
                      : categories.find(c => c.id === category.parent_cat_id)?.name || 'Unknown'
                    }
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {category.created_at}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex space-x-2">
                      <button
                        onClick={() => handleEdit(category)}
                        className="text-primary-600 hover:text-primary-900"
                      >
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(category)}
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
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                {editingCategory ? 'Edit Category' : 'Add New Category'}
              </h3>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Category Name
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
                    Type
                  </label>
                  <select
                    className="input-field"
                    value={formData.type}
                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  >
                    <option value="1">Main Category</option>
                    <option value="2">Sub Category</option>
                  </select>
                </div>

                {formData.type === '2' && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Parent Category
                    </label>
                    <select
                      className="input-field"
                      value={formData.parent_cat_id}
                      onChange={(e) => setFormData({ ...formData, parent_cat_id: e.target.value })}
                    >
                      <option value="0">Select Parent Category</option>
                      {mainCategories.map((cat) => (
                        <option key={cat.id} value={cat.id}>
                          {cat.name}
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Image
                  </label>
                  <input
                    type="file"
                    accept="image/*"
                    className="input-field"
                    onChange={(e) => setFormData({ ...formData, image: e.target.files?.[0] || null })}
                  />
                </div>

                <div className="flex justify-end space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={() => {
                      setShowModal(false)
                      setEditingCategory(null)
                      setFormData({ name: '', parent_cat_id: '0', type: '1', image: null })
                    }}
                    className="btn-secondary"
                  >
                    Cancel
                  </button>
                  <button type="submit" className="btn-primary">
                    {editingCategory ? 'Update' : 'Create'}
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