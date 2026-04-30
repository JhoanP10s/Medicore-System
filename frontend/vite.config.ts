import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/paciente': 'http://localhost:8080',
      '/medico': 'http://localhost:8080',
      '/cita': 'http://localhost:8080',
      '/especialidad': 'http://localhost:8080'
    }
  }
});
