/* eslint-disable no-undef */
import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { NaiveUiResolver } from 'unplugin-vue-components/resolvers'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'


// https://vitejs.dev/config/
export default defineConfig({
  define: { 'process.env': process.env },
  plugins: [
    vue({
      template: {
        compilerOptions: {
          // 所有以 mdui- 开头的标签名都是 mdui 组件
          isCustomElement: (tag) => tag.startsWith('mdui-')
        }
      }
    }),
    AutoImport({ imports: ['vue'], resolvers: [ElementPlusResolver()] }),
    Components({ resolvers: [ElementPlusResolver(),NaiveUiResolver()] }),
  ],
  server: { host: '0.0.0.0' },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
