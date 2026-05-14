import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    proxy: {
      // Mọi request gọi vào '/api' sẽ được Vite ngầm chuyển qua Backend 8080
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})