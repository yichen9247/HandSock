/**
 * Theme Utility Module
 * 
 * Handles theme management and dynamic styling including:
 * - Theme switching between default, refresh, pureshs and yalansh
 * - Dynamic CSS variable updates
 * - Background image loading
 * - Local storage persistence
 */

import utils from './utils'
import socket from '@/socket/socket'

export const setDeviceTheme = (): void => {
    const theme = localStorage.getItem('theme');
    if (theme === 'default') {
        document.body.style.setProperty("--dominColor", socket.dominColor.defaultDominColor);
        !utils.isMobile() && import('@/assets/paper/background-1.jpg').then((image): any => document.body.style.setProperty("--background", `url(${image.default})`));
    } else 
    if (theme === 'refresh') {
        document.body.style.setProperty("--dominColor", socket.dominColor.refreshDominColor);
        !utils.isMobile() && import('@/assets/paper/background-0.jpg').then((image): any => document.body.style.setProperty("--background", `url(${image.default})`));
    } else 
    if (theme === 'pureshs') {
        document.body.style.setProperty("--dominColor", socket.dominColor.pureshsDominColor);
        !utils.isMobile() && import('@/assets/paper/background-3.jpg').then((image): any => document.body.style.setProperty("--background", `url(${image.default})`));
    } else 
    if (theme === 'yalansh') {
        document.body.style.setProperty("--dominColor", socket.dominColor.yalanshDominColor);
        !utils.isMobile() && import('@/assets/paper/background-4.jpg').then((image): any => document.body.style.setProperty("--background", `url(${image.default})`));
    } else {
		localStorage.setItem('theme', 'default');
        document.body.style.setProperty("--dominColor", socket.dominColor.defaultDominColor);
        !utils.isMobile() && import('@/assets/paper/background-1.jpg').then((image): any => document.body.style.setProperty("--background", `url(${image.default})`));
    }
}