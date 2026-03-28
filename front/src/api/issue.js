import request from '../utils/request'
export default {
    report(data) { return request.post('/issue/report', data) },
    page(params) { return request.get('/issue/page', { params }) },
    detail(id) { return request.get(`/issue/detail/${id}`) },
    assign(data) { return request.post('/issue/assign', data) },
    transfer(data) { return request.post('/issue/transfer', data) },
    updateStatus(data) { return request.post('/issue/updateStatus', data) },
    // 删除问题（管理员）
    delete(id) {
        return request({
            url: '/issue/delete/' + id,
            method: 'post'
        })
    }
}