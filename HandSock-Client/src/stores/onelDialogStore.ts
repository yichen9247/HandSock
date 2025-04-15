/**
 * Dialog Store Module
 * 
 * Manages dialog and modal states using Pinia store
 * Controls visibility of various UI components like sidebars, panels and modals
 */
import { defineStore } from "pinia"

export const useOnelDialogStore = defineStore('onelDialogStore', () => {
    // UI component states
    const forumPostDetail: Ref<boolean> = ref(false);
    const userLoginCenter: Ref<boolean> = ref(false);
    const adminFrameStatus: Ref<boolean> = ref(false);

    // State setters
    const setUserLoginCenter = (value: boolean) => userLoginCenter.value = value;

    // State setters
    const setForumPostDetail = (value: boolean) => forumPostDetail.value = value;

    // Toggle functions
    const toggleAdminFrameStatus = (): boolean => adminFrameStatus.value = !adminFrameStatus.value;

    return ({ userLoginCenter, adminFrameStatus, forumPostDetail, toggleAdminFrameStatus, setUserLoginCenter, setForumPostDetail });
});