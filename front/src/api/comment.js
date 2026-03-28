import request from '../utils/request'
export default {
    add(data) { return request.post('/comment/add', data) },
    list(issueId) { return request.get(`/comment/list/${issueId}`) }
}