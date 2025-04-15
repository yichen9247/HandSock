import utils from "./utils";
import socket from "@/socket/socket";
import {addPostCommentType, forumCommentType, forumPostType, noticeBoardType, restfulType} from "../../types";

const delayTime: number = 200;

export default class RequestUtils {
    static addForumComment = async (formData: addPostCommentType): Promise<any> => {
        const applicationStore = utils.useApplicationStore();
        return new Promise((res): void => {
            applicationStore.socketIo.emit(socket.send.Forum.AddCommentPost, formData, async (response: restfulType<Array<forumCommentType>>): Promise<void> => {
                if (response.code === 200) {
                    setTimeout(() => {
                        res(response.data);
                    }, delayTime);
                } else await utils.showToasts('error', response.message);
            })
        });
    }

    static getNoticeList = async (): Promise<any> => {
        const applicationStore = utils.useApplicationStore();
        return new Promise((res): void => {
            applicationStore.socketIo.emit(socket.send.Search.All.SearchNoticeAll, {}, async (response: restfulType<Array<noticeBoardType>>): Promise<void> => {
                if (response.code === 200) {
                    setTimeout(() => {
                        res(response.data);
                    }, delayTime);
                } else await utils.showToasts('error', response.message);
            })
        });
    }

    static getForumPostList = async (page: number, limit: number): Promise<any> => {
        const applicationStore = utils.useApplicationStore();
        return new Promise((res): void => {
            applicationStore.socketIo.emit(socket.send.Search.SearchForumPost, {
                page: page,
                limit: limit
            }, async (response: restfulType<Array<forumPostType>>): Promise<void> => {
                if (response.code === 200) {
                    setTimeout(() => {
                        res(response.data);
                    }, delayTime);
                } else await utils.showToasts('error', response.message);
            })
        });
    }

    static getForumCommentList = async (pid: number, page: number, limit: number): Promise<any> => {
        const applicationStore = utils.useApplicationStore();
        return new Promise((res): void => {
            applicationStore.socketIo.emit(socket.send.Search.SearchForumComment, {
                pid: pid,
                page: page,
                limit: limit
            }, async (response: restfulType<Array<forumCommentType>>): Promise<void> => {
                if (response.code === 200) {
                    setTimeout(() => {
                        res(response.data);
                    }, delayTime);
                } else await utils.showToasts('error', response.message);
            })
        });
    }
}