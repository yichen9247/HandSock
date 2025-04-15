export interface restfulType<T> {
    data: T,
    code: number,
    message: string
}

export interface arrayDataType<T> {
    total: number,
    items: Array<T>
}

export interface userInfoType {
    uid: string,
    nick: string,
    avatar: string,
    status: number,
    username: string
    permission: number
}

export interface groupInfoType {
    gid: number,
    name: string,
    open: boolean,
    avatar: string,
    notice: string,
    aiRole: boolean,
    active: boolean
}

export interface messageType {
    uid: string,
    gid: number,
    sid: string,
    type: string,
    time: string,
    deleted: number,
    address: string,
    content: string
}

export interface onlineUserType {
    uid: string,
    uuids: string,
    login: boolean,
    platform: string
}

export interface loginFormType {
    username: string,
    password: string
}

export interface reportFormType {
    sid: string,
    reason: string,
    reportedId: string
}

export interface addPostFormType {
    title: string,
    content: string,
    image: Array<string>
}

export interface addPostCommentType {
    pid: number,
    content: string,
    parent: null | number
}

export interface sidebarListType {
    call: any,
    name: string,
    icon: string
}

export interface adminChanFormType {
    gid: number,
    name: string,
    notice: string,
    avatar: string,
    aiRole: boolean
}

export interface adminBannerFormType {
    bid: number,
    name: string,
    href: string,
    image: string
}

export interface adminNoticeFormType {
    nid: number,
    title: string,
    content: string
}

export interface adminUserFormType {
    uid: string,
    nick: string,
    robot: boolean,
    avatar: string,
    username: string
}

export interface adminSystemInfoType {
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

export interface adminSystemMannerType {
    taboo: boolean,
    upload: boolean,
    register: boolean,
    version: string,
    download: string
}

export interface aiEventHandleType {
    event: string,
    eventId: string,
    content: string,
    result: messageType
}

export interface systemInfoType {
    name: string,
    value: string,
    status: string
}

export interface noticeBoardType {
    nid: number,
    time: string,
    title: string,
    content: string
}

export interface forumPostType {
    pid: number,
    uid: number,
    time: string,
    title: string,
    image: string,
    content: string,
    user: userInfoType
}

export interface uploadStatusType {
    path: string
}

export interface userAuthStatus {
    token: string,
    userinfo: userInfoType
}

export interface forumCommentType {
    pid: number,
    cid: number,
    time: string,
    content: string,
    user: userInfoType,
    children: Array<forumCommentType>
}