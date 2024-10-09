
import Vant from 'vant'
import './assets/index.css'
import 'vant/lib/index.css'
import '@/assets/paces/pace.css'
import 'element-plus/dist/index.css'

import router from "@/router"
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import HandSock from './HandSock.vue'
const application = createApp(HandSock);

Promise.all([
    application.use(Vant),
    application.use (router),
    application.use(createPinia())
]).then(async () => {
    application.mount('#application');
});