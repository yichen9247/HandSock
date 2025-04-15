/**
 * Router Configuration Module
 * 
 * This module configures Vue Router and handles route-related functionality including:
 * - Route definitions and navigation guards
 * - Page loading progress indicators via Pace.js
 * - Initial theme and audio settings
 */

/* eslint-disable no-undef */

import Pace from 'pace-js'
import socket from '@/socket/socket'
import HomeView from '@/pages/HomeView.vue'
import ThemeUtils from '@/scripts/ThemeUtils'
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const siteLoadComplete = async (): Promise<void> => {
	Pace.on("done", async (): Promise<void> => {
		document.body.className = ""
	});
}

Pace.on("start", async (): Promise<void> => {
	ThemeUtils.setDeviceTheme();
	if (!localStorage.getItem("audio")) localStorage.setItem('audio', 'default');
	if (!localStorage.getItem("audio-switch")) localStorage.setItem("audio-switch", "true");
});

const routes: readonly RouteRecordRaw[] = [
	{ 
		path: '/', 
		name: 'home', 
		component: HomeView, 
		beforeEnter: [siteLoadComplete] 
	},
	{ 
		path: "/:catchAll(.*)", 
		component: HomeView, 
		beforeEnter: [siteLoadComplete] 
	}
];

const router = createRouter({
	routes,
	history: createWebHistory(socket.server.config.baseUrl),
	scrollBehavior(to, from, savedPosition) {
		if (savedPosition) return savedPosition;
		if (to.meta.scrollToTop) return { top: 0 };
		if (to.hash) return { el: to.hash, behavior: 'smooth' };
	},
});

router.beforeEach(async (to, from, next): Promise<void> => {
	if (to !== from) next();
});

export default router;