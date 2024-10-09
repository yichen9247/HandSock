

import { ref } from "vue"
import utils from "@/scripts/utils"
import { defineStore } from "pinia"

onresize = async () => {
  let state = utils.isMobile();
  const applicationStore = useApplicationStore();
  applicationStore.setIsDeviceMobile(state);
}

export const useApplicationStore = defineStore('applicationStore',() => {
  const serverToken = ref(null);
  const isDeviceMobile = ref(utils.isMobile());
  const isSiteReadyStatus = ref(false);

  const setServerToken = (value) => serverToken.value = value;
  const setIsDeviceMobile = (value) => isDeviceMobile.value = value;
  const setIsSiteReadyStatus = (value) => isSiteReadyStatus.value = value;

  return ({ serverToken, isDeviceMobile, isSiteReadyStatus, setServerToken, setIsDeviceMobile, setIsSiteReadyStatus });
});