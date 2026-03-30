import request from '../utils/request'

// 未读数量
export function getUnreadCount(userId) {
    return request({
        url: '/msg/unread/count',
        params: { userId }
    })
}

// 消息列表
export function getMsgList(userId) {
    return request({
        url: '/msg/list',
        params: { userId }
    })
}

// 标记单条已读
export function readMsg(id) {
    return request({
        url: `/msg/read/${id}`,
        method: 'post'
    })
}

// 一键已读
export function readAllMsg(userId) {
    return request({
        url: '/msg/read/all',
        method: 'post',
        params: { userId }
    })
}