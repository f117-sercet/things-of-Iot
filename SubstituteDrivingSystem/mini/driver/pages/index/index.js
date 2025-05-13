

Page({

  /**
   * 页面的初始数据
   */
  data: {
   longtitude:null,
   latitude:null,
   loading:true,
   active:0,
   driverProcessing:false,
   driverStart:true

  },


  onChange(event){
    this.setData({
   active:event.detail
    })
    console.log("event",event.detail)
    console.log("active",this.data.active)
  },


  // 司机开始接单 test1
  startTakingOrdersHandle(){
    let that = this
    that.setData({
      driverStart:false,
      driverProcessing:true
    })
  

  },



  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
  let that = this
    wx.getLocation({
      type: 'gcj02',
      success(res) {
        console.log(res)
      that.setData({
          latitude: res.latitude,
          longtitude: res.longitude
      })
        console.log("纬度",that.data.latitude)
        console.log("精度",that.data.longtitude)
      }
  })
},

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

    console.log("123")

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})