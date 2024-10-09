
import { ref } from "vue"
import { defineStore } from "pinia"

export const useOnelDialogStore = defineStore('onelDialogStore',() => {
    const mobilesidebars = ref(false);
    const personalCenter = ref(false);
    const searchalCenter = ref(false);
    const settinglCenter = ref(false);
    
    const setMobilesidebars = (value) => mobilesidebars.value = value;
    const setPersonalCenter = (value) => personalCenter.value = value;
    const setSearchalCenter = (value) => searchalCenter.value = value;
    const setSettinglCenter = (value) => settinglCenter.value = value;

    return ({ mobilesidebars, personalCenter, searchalCenter, setMobilesidebars, settinglCenter, setPersonalCenter, setSearchalCenter, setSettinglCenter });
});