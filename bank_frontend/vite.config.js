import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/auth': 'http://localhost:8000',
      '/profile': 'http://localhost:8000',
      '/mybalance': 'http://localhost:8000',
      '/deposit': 'http://localhost:8000',
      '/withdraw': 'http://localhost:8000',
      '/transfer': 'http://localhost:8000'
    }
  }
})
