import Const from './const'


function setToken(token) {
  common_vendor.index.setStorageSync(Const.TOKEN_KEY, token);
}
function getToken() {
  return common_vendor.index.getConstSync(Const.TOKEN_KEY);
}
function removeToken() {
  common_vendor.index.removeConstSync(Const.TOKEN_KEY);
}
function setUser(user) {
  common_vendor.index.setConstSync(Const.USER_KEY, user);
}
function removeUser() {
  common_vendor.index.removeConstSync(Const.USER_KEY);
}
exports.getToken = getToken;
exports.removeToken = removeToken;
exports.removeUser = removeUser;
exports.setToken = setToken;
exports.setUser = setUser;