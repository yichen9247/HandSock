/**
 * Storage Utility Module
 * 
 * Provides helper functions for managing localStorage:
 * - saveLocalStorage: Saves multiple key-value pairs to localStorage
 * - removeLocalStorage: Removes multiple keys from localStorage
 */

export const saveLocalStorage = async (keys: Array<string>, values: Array<string>) => {
    for (let index: number = 0; index < keys.length; index++) {
        localStorage.setItem(keys[index], values[index]);
    }
}

export const removeLocalStorage = async (keys: Array<string>) => {
    for (let index: number = 0; index < keys.length; index++) {
        localStorage.removeItem(keys[index]);
    }
}