// app.js
App({
  onLaunch() {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    //全局分享
    this.overShare()
    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
    //版本更新提示
    this.globalData.sysinfo = wx.getSystemInfoSync()
    const updateManager = wx.getUpdateManager()
    updateManager.onCheckForUpdate(function (res) {
      // 请求完新版本信息的回调
      console.log(res.hasUpdate)
    })
    updateManager.onUpdateReady(function () {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success: function (res) {
          if (res.confirm) {
            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
            updateManager.applyUpdate()
          }
        }
      })
    })
    updateManager.onUpdateFailed(function () {
      // 新的版本下载失败
      wx.showModal({
        title: '更新提示',
        content: '新版本下载失败',
        showCancel: false
      })
    })
  },
  // 全局添加分享
  overShare() {
    wx.onAppRoute(res => {
      let pages = getCurrentPages()
      let view = pages[pages.length - 1]
      if (view) {
        view.onShareAppMessage = res => {
          return {
            title: getApp().globalData.shareTitle, //  标题
            path: '/pages/login/login',
            imageUrl: getApp().globalData.shareImg,
            success: function (res) {}
          }
        }
      } else {}
    })
  },
  globalData: {
    shareTitle: '代驾小程序',
    shareImg: '',
    sysinfo: {},
  }
})