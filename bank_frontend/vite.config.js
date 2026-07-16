import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/auth': 'http://localhost:8080',
      '/profile': 'http://localhost:8080',
      '/mybalance': 'http://localhost:8080',
      '/deposit': 'http://localhost:8080',
      '/withdraw': 'http://localhost:8080',
      '/transfer': 'http://localhost:8080'
    }
  }
})
