export default class StorageUtils {
    static saveLocalStorage = async (keys: Array<string>, values: Array<string>) => {
        for (let index: number = 0; index < keys.length; index++) {
            localStorage.setItem(keys[index], values[index]);
        }
    }
    
    static removeLocalStorage = async (keys: Array<string>) => {
        for (let index: number = 0; index < keys.length; index++) {
            localStorage.removeItem(keys[index]);
        }
    }
}