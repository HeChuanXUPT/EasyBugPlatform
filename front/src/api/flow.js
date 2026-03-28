import request from '../utils/request'
export default {
  list(issueId) { return request.get(`/flow/list/${issueId}`) }
}