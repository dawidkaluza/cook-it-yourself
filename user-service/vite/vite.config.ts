import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 9090,
    strictPort: true,
  },
  preview: {
    port: 9090,
    strictPort: true,
  },
  plugins: [react()],
})
