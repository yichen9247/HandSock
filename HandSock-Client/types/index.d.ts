export type userInfoType = {
    uid: string,
    nick: string,
    email: string,
    avatar: string,
    isAdmin: number,
    isRobot: number,
    username: string
}

export type groupInfoType = {
    gid: number,
    name: string,
    open: boolean
    avatar: string,
    notice: string,
    aiRole: boolean
}

export type messageType = {
    uid: string,
    gid: number,
    sid: string,
    type: string,
    time: string,
    deleted: number,
    address: string,
    content: string
}

export type onlineUserType = {
    uid: string,
    uuids: string,
    login: boolean,
    platform: string
}

export type restfulType = {
    data: any,
    code: number,
    message: string
}

export type loginFormType = {
    username: string,
    password: string
}

export type reportFormType = {
    sid: string,
    reason: string,
    reported_id: string
}

export type sidebarListType = {
    call: any,
    name: string,
    icon: string
}

export type adminChanFormType = {
    gid: number,
    name: string,
    notice: string,
    avatar: string,
    aiRole: boolean
}

export type adminUserFormType = {
    uid: string,
    nick: string,
    robot: boolean,
    avatar: string,
    username: string
}

export type adminSystemInfoType = {
    osInfo: string,
    osArch: string,
    locale: string,
    hostName: string,
    appVersion: string,
    timeZoneId: string,
    hostAddress: string,
    systemUptime: string,
    logicalCount: number,
    memoryUsageInfo: string
}

export type adminSystemMannerType =  {
    taboo: boolean,
    upload: boolean,
    playlist: string,
    register: boolean
}