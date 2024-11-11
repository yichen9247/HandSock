/* eslint-disable no-undef */
import Pace from 'pace-js'
import config from '@/scripts/config'
import HomeView from '@/pages/HomeView.vue'
import themeUtils from '@/scripts/themeUtils'
import { createRouter, createWebHistory } from 'vue-router'
import { useApplicationStore } from '@/stores/applicationStore'

Pace.options = { ajax: ['ws', /ws/] }

const siteLoadComplete = async () => {
	const applicationStore = useApplicationStore();
	Pace.on("done", async () => {
		await applicationStore.setIsSiteReadyStatus(true);
		setTimeout(async () => document.querySelector(".chat-content-box").scrollTo({ top: document.querySelector(".chat-content-box").scrollHeight, behavior: 'smooth' }), 500);
	});
}

Pace.on("start", async () => {
	document.body.style.setProperty("--dominColor", config.defaultDominColor);
	await themeUtils.setDeviceTheme();
	if (localStorage.getItem("audio") === null) localStorage.setItem('audio', 'default');
	if (localStorage.getItem("audio-switch") === null) localStorage.setItem("audio-switch", "true");
});

const routerPath = [
	{ path: '/', name: 'home', component: HomeView, beforeEnter: [siteLoadComplete] },
	{ path: "/:catchAll(.*)", component: HomeView, beforeEnter: [siteLoadComplete] }
];

const createRoutes = createRouter({
	routes: routerPath,
	history: createWebHistory(import.meta.env.BASE_URL),
	scrollBehavior(to, from, savedPosition) {
		if (savedPosition) return savedPosition;
		if (to.meta.scrollToTop) return { top: 0 };
		if (to.hash) return { el: to.hash, behavior: "smooth" };
	},
});

createRoutes.beforeEach(async (to, from, next) => {
	if (to === from) return;
	next();
});

export default createRoutes;
