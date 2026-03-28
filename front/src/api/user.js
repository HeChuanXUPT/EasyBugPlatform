import request from '../utils/request'
export default {
  login(data) { return request.post('/user/login', data) },
  list() { return request.get('/user/list') },
  add(data) { return request.post('/user/add', data) },
  del(id) { return request.delete(`/user/delete/${id}`) }
}