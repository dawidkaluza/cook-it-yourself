/// <reference types="vitest" />
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, './');
  const publicPath = env.VITE_PUBLIC_PATH;

  return {
    base: publicPath,
    server: {
      port: 9090,
        strictPort: true,
    },
    preview: {
      port: 9090,
        strictPort: true,
    },
    plugins: [react()],
    test: {
      environment: 'jsdom',
      globals: true,
    },
  };
})
