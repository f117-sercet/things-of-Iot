import { storageKey } from './const'

var environment = 'prod';//生产
// var environment = 'test';//模拟
// const environment = 'dev';//线下

//正常的
const configs = {
  //线下环境
  dev: {
    apiBase: 'http://192.168.2.63:8090',//段
    // apiBase: 'http://192.168.2.47:8090',//王
    //apiBase: 'http://192.168.2.90:8090',//刘
    // apiBase: 'http://192.168.2.118:8090'//白
    // apiBase: 'http://192.168.2.213:8090'//史
  },
  //模拟环境
  test: {
    apiBase: 'http://192.168.0.42:8090',
  },
  //生产环境
  prod: {
    // apiBase: 'http://1.71.135.234:8090',//ip
    apiBase: 'https://smpt.sxymlc.com/prod-api',//域名
  },
};
const config = configs[environment];

module.exports = {
  config: config,

  http(url, method, params) {
    // let token = 'token' // 获取token，自行获取token和签名，token和签名表示每个接口都要发送的数据
    // let sign = 'sign' // 获取签名
    // let accId='';
    let loginToken = '';
    let uId = '';
    let deptId = '';
    if (wx.getStorageSync(storageKey.key_loginToken)) {
      loginToken = wx.getStorageSync(storageKey.key_loginToken)
    }
    if (wx.getStorageSync(storageKey.key_loginUserInfo)) {
      uId = wx.getStorageSync(storageKey.key_loginUserInfo).data.userId
      deptId = wx.getStorageSync(storageKey.key_loginUserInfo).data.deptId
    }
    console.log("loginToken==" + loginToken)
    let data = {
      // loginToken
      /*      'type':'Mini',*/
    }

    if (params.data) { // 在这里判断一下data是否存在，params表示前端需要传递的数据，params是一个对象，有三组键值对，data：表示请求要发送的数据，success：成功的回调，fail：失败的回调，这三个字段可缺可无，其余字段会忽略
      for (let key in params.data) { // 在这里判断传过来的参数值为null，就删除这个属性
        if (params.data[key] == null || params.data[key] == 'null') {
          delete params.data[key]
        }
      }
      data = { ...data, ...params.data }
    }
    let header = {
      //'loginType':'Mini',
      // 改成type
      'type': 'Mini',
      Authorization: loginToken,
      // Authorization: 'Ymlc112233@@qqwwee'
      // deptId: deptId
    }

    wx.request({
      url: config.apiBase + url,
      method: method, // 判断请求类型，除了值等于'post'外，其余值均视作get
      data,
      header: header,

      success(res) {
        // console.log(res)
        if (res.statusCode === 500) {
          wx.showToast({
            title: '服务器请求失败',
            icon: 'none',
          })
        } else {
          if (res.data.code === 401) {
            wx.showToast({
              title: '登录过期，请重新登录',
              icon: 'none',
            })
            wx.setStorageSync(storageKey.key_login, false);
            wx.removeStorageSync(storageKey.key_loginUserInfo);
            wx.removeStorageSync(storageKey.key_loginToken);
            setTimeout(() => {
              wx.redirectTo({
                url: '/pages/login/login',
              })
            }, 1000);
          } else {
            params.success && params.success(res.data)
          }
        }
      },
      fail(err) {
        params.fail && params.fail(err)
      }
    })
    console.log(url, header, data);
  },

  /**
   * 上传图片
   */
  uploadImage(url, filePath, params) {
    wx.showLoading({
      title: '努力加载中...',
      mask: true,
    })
    let loginToken = '';
    if (wx.getStorageSync(storageKey.key_loginToken)) {
      loginToken = wx.getStorageSync(storageKey.key_loginToken)
    }
    let header = {
      // 改成type
      'type': 'Mini',
      Authorization: loginToken
      // Authorization: 'Ymlc112233@@qqwwee'
    }
    let formData = {
    }
    if (params.formData) { // 在这里判断一下data是否存在，params表示前端需要传递的数据，params是一个对象，有三组键值对，data：表示请求要发送的数据，success：成功的回调，fail：失败的回调，这三个字段可缺可无，其余字段会忽略
      for (let key in params.formData) { // 在这里判断传过来的参数值为null，就删除这个属性
        if (params.formData[key] == null || params.formData[key] == 'null') {
          delete params.formData[key]
        }
      }
      formData = { ...formData, ...params.formData }
    }
    wx.uploadFile({
      url: config.apiBase + url,
      method: 'POST',
      name: 'file',
      filePath: filePath,
      // formData: formData,
      header: header,
      formData,
      success(res) {
        wx.hideLoading();
        if (res.statusCode === 500) {
          wx.showToast({
            title: '服务器请求失败',
            icon: 'none',
          })
        } else {
          if (res.data.code === 401) {
            wx.showToast({
              title: '登录过期，请重新登录',
              icon: 'none',
            })
            wx.setStorageSync(storageKey.key_login, false);
            wx.removeStorageSync(storageKey.key_loginUserInfo);
            wx.removeStorageSync(storageKey.key_loginToken);
            setTimeout(() => {
              wx.redirectTo({
                url: '/pages/login/login',
              })
            }, 1000);
          } else {
            params.success && params.success(res.data)
          }
        }
      },
      fail: function (e) {
        // fail && fail(e)
        params.fail && params.fail(e)
      }
    })
  }
}
