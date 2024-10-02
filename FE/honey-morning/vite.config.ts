import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react-swc';
import {VitePWA} from 'vite-plugin-pwa';
import path from 'path';

const __dirname = path.resolve();

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      devOptions: {
        enabled: true,
      },
      manifest: {
        name: 'Honey Morning',
        short_name: 'HoneyMorning',
        description: 'Honey Morning with SSAFY NiKKA',
        theme_color: '#ffffff',
        background_color: '#ffffff',
        display: 'standalone',
        scope: '/',
        start_url: '/',
        icons: [
          {
            src: '/favicon.ico',
            sizes: '64x64 32x32 24x24 16x16',
            type: 'image/x-icon',
          },
          {
            src: '/images/tempAppIcon192x192.png',
            type: 'image/png',
            sizes: '192x192',
          },
          {
            src: '/images/tempAppIcon512x512.png',
            type: 'image/png',
            sizes: '512x512',
          },
        ],
      },
    }),
  ],
  publicDir: 'public', // public 폴더를 명시적으로 설정
  resolve: {
    alias: [
      {find: '@', replacement: path.resolve(__dirname, 'src')},
      {find: '@api', replacement: path.resolve(__dirname, 'src/api')},
      {find: '@assets', replacement: path.resolve(__dirname, 'src/assets')},
      {
        find: '@component',
        replacement: path.resolve(__dirname, 'src/component'),
      },
      {find: '@pages', replacement: path.resolve(__dirname, 'src/pages')},
      {find: '@router', replacement: path.resolve(__dirname, 'src/router')},
      {find: '@store', replacement: path.resolve(__dirname, 'src/store')},
    ],
  },
});
