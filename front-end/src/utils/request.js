import axios from 'axios'

const service = axios.create({
  baseURL: '/api', // Use relative path to leverage Vite proxy
  timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    // Add token if available
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers['Authorization'] = 'Bearer ' + token
    // }
    return config
  },
  (error) => {
    console.log(error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // Assuming backend returns { code: 200, data: ..., message: ... }
    if (res.code !== 200) {
      // Handle error
      console.error('Error:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res.data
    }
  },
  (error) => {
    console.log('err' + error)
    return Promise.reject(error)
  }
)

export default service
