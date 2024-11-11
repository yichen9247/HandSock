
import config from './config';

const setDeviceTheme = async () => {
    const theme = localStorage.getItem('theme');
    if (theme === 'default') {
        document.body.style.setProperty("--dominColor", config.dominColor.defaultDominColor);
        document.body.style.setProperty("--dominColorV2", config.dominColor.defaultDominColorV2);
        import('@/assets/paper/background-1.jpg').then((image) => document.body.style.setProperty("--background", `url(${image.default})`));
    } else 
    if (theme === 'refresh') {
        document.body.style.setProperty("--dominColor", config.dominColor.refreshDominColor);
        document.body.style.setProperty("--dominColorV2", config.dominColor.refreshDominColorV2);
        import('@/assets/paper/background-0.jpg').then((image) => document.body.style.setProperty("--background", `url(${image.default})`));
    } else 
    if (theme === 'pureshs') {
        document.body.style.setProperty("--dominColor", config.dominColor.pureshsDominColor);
        document.body.style.setProperty("--dominColorV2", config.dominColor.pureshsDominColorV2);
        import('@/assets/paper/background-3.jpg').then((image) => document.body.style.setProperty("--background", `url(${image.default})`));
    } else 
    if (theme === 'yalansh') {
        document.body.style.setProperty("--dominColor", config.dominColor.yalanshDominColor);
        document.body.style.setProperty("--dominColorV2", config.dominColor.yalanshDominColorV2);
        import('@/assets/paper/background-4.jpg').then((image) => document.body.style.setProperty("--background", `url(${image.default})`));
    } else {
		localStorage.setItem('theme', 'default');
        document.body.style.setProperty("--dominColor", config.dominColor.defaultDominColor);
        document.body.style.setProperty("--dominColorV2", config.dominColor.defaultDominColorV2);
        import('@/assets/paper/background-1.jpg').then((image) => document.body.style.setProperty("--background", `url(${image.default})`));
    }
}

export default { setDeviceTheme }