/**
 * Main Application Entry Point
 * 
 * Initializes and configures the Vue application with required plugins and dependencies.
 * Sets up routing, state management, UI components and development tools.
 */

import 'vant/lib/index.css'
import 'element-plus/dist/index.css'

import Vant from 'vant'
import router from "@/router"
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import socket from './socket/socket'
import HandSock from './HandSock.vue'
import DisableDevtool from 'disable-devtool'

const application = createApp(HandSock);
// if (import.meta.env.MODE === 'production') DisableDevtool(socket.devtool);

Promise.all([
    application.use(Vant),
    application.use(router),
    application.use(createPinia())
]).then(() => {
    application.mount('#application');
});