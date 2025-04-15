import utils from './utils'
import socket from '@/socket/socket'

export default class ThemeUtils {
    static setDeviceTheme = async (): Promise<void> => {
        let theme = localStorage.getItem('theme')
        let color: string
        let imageImport: Promise<{ default: string }>

        if (theme === 'default') {
            color = socket.dominColor.defaultDominColor
            imageImport = import('@/assets/paper/background-0.jpg')
        } else if (theme === 'pureshs') {
            color = socket.dominColor.pureshsDominColor
            imageImport = import('@/assets/paper/background-1.jpg')
        } else if (theme === 'yalansh') {
            color = socket.dominColor.yalanshDominColor
            imageImport = import('@/assets/paper/background-2.jpg')
        } else if (theme === 'roufenh') {
            color = socket.dominColor.roufenhDominColor
            imageImport = import('@/assets/paper/background-3.jpg')
        } else {
            localStorage.setItem('theme', 'default')
            color = socket.dominColor.defaultDominColor
            imageImport = import('@/assets/paper/background-0.jpg')
        }

        try {
            if (!utils.isMobile()) {
                const image = await imageImport
                await this.preloadImage(image.default)
            }
            document.body.style.setProperty("--dominColor", color)
            if (!utils.isMobile()) {
                const image = await imageImport
                document.body.style.setProperty("--background", `url(${image.default})`)
            }
        } catch (e) {
            document.body.style.setProperty("--dominColor", color)
        }
    }

    private static preloadImage = (url: string): Promise<void> => {
        return new Promise((resolve, reject) => {
            const img = new Image()
            img.src = url
            img.onload = () => resolve()
            img.onerror = (e) => reject(e)
        });
    }
}