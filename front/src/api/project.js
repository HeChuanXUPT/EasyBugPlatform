import request from '../utils/request'
export default {
  list() { return request.get('/project/list') },
  add(data) { return request.post('/project/add', data) },
  del(id) { return request.delete(`/project/delete/${id}`) }
}