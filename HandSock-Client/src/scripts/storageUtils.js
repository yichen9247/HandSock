
export const saveLocalStorage = async (keys, values) => {
    for (let index = 0; index < keys.length; index++) {
        localStorage.setItem(keys[index], values[index]);
    }
}

export const removeLocalStorage = async (keys) => {
    for (let index = 0; index < keys.length; index++) {
        localStorage.removeItem(keys[index]);
    }
}