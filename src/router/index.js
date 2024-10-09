/* eslint-disable no-undef */
import Pace from 'pace-js'
import theme from '@/scripts/theme'
import config from '@/scripts/config'
import HomeView from '@/pages/HomeView.vue'
import { useApplicationStore } from '@/stores/application'
import { createRouter, createWebHistory } from 'vue-router'

const siteLoadComplete = async () => {
  const applicationStore = useApplicationStore();
  Pace.on("done", async () => {
    await applicationStore.setIsSiteReadyStatus(true);
    setTimeout(async () => document.querySelector(".content-main").scrollTo({ top: document.querySelector(".content-main").scrollHeight, behavior: 'smooth' }), 500);
  });
}

Pace.on("start", async () => {
	document.body.style.setProperty("--dominColor", config.defaultDominColor);
	await theme.setDeviceTheme();
});

const routerPath = [{
  path: '/',
  name: 'home',
  component: HomeView,
  beforeEnter: [siteLoadComplete],
}];

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
