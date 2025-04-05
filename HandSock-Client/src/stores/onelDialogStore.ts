/**
 * Dialog Store Module
 * 
 * Manages dialog and modal states using Pinia store
 * Controls visibility of various UI components like sidebars, panels and modals
 */
import { defineStore } from "pinia"

export const useOnelDialogStore = defineStore('onelDialogStore', () => {
    // UI component states
    const personalCenter: Ref<boolean> = ref(false); 
    const searchalCenter: Ref<boolean> = ref(false);
    const settinglCenter: Ref<boolean> = ref(false);
    const userLoginCenter: Ref<boolean> = ref(false);
    const emojeListCenter: Ref<boolean> = ref(false);
    const adminFrameStatus: Ref<boolean> = ref(false);
    const reportUserCenter: Ref<boolean> = ref(false);

    // State setters
    const setPersonalCenter = (value: boolean) => personalCenter.value = value;
    const setSearchalCenter = (value: boolean) => searchalCenter.value = value;
    const setSettinglCenter = (value: boolean) => settinglCenter.value = value;
    const setUserLoginCenter = (value: boolean) => userLoginCenter.value = value;
    const setEmojeListCenter = (value: boolean) => emojeListCenter.value = value;
    const setReportUserCenter = (value: boolean) => reportUserCenter.value = value;

    // Toggle functions
    const toggleAdminFrameStatus = (): boolean => adminFrameStatus.value = !adminFrameStatus.value;

    return ({ personalCenter, searchalCenter, userLoginCenter, reportUserCenter, settinglCenter, emojeListCenter, adminFrameStatus, setPersonalCenter, setSearchalCenter, setSettinglCenter, setEmojeListCenter, setReportUserCenter, toggleAdminFrameStatus, setUserLoginCenter });
});