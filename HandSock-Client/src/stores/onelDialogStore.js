
import { ref } from "vue"
import { defineStore } from "pinia"

export const useOnelDialogStore = defineStore('onelDialogStore',() => {
    const mobilesidebars = ref(false);
    const personalCenter = ref(false);
    const searchalCenter = ref(false);
    const settinglCenter = ref(false);
    const emojeListCenter = ref(false);
    const adminFrameStatus = ref(false);
    const uploadFileCenter = ref(false);
    
    const setMobilesidebars = (value) => mobilesidebars.value = value;
    const setPersonalCenter = (value) => personalCenter.value = value;
    const setSearchalCenter = (value) => searchalCenter.value = value;
    const setSettinglCenter = (value) => settinglCenter.value = value;
    const setEmojeListCenter = (value) => emojeListCenter.value = value;
    const setUploadFileCenter = (value) => uploadFileCenter.value = value;
    const toggleAdminFrameStatus = () => adminFrameStatus.value = !adminFrameStatus.value;

    const toggleFloatSheet = () => {
        mobilesidebars.value = false;
    }

    return ({ mobilesidebars, personalCenter, searchalCenter, setMobilesidebars, settinglCenter, emojeListCenter, uploadFileCenter, adminFrameStatus, setPersonalCenter, setSearchalCenter, setSettinglCenter, setEmojeListCenter, setUploadFileCenter, toggleAdminFrameStatus, toggleFloatSheet });
});