// page/download/download.js
Page({

  didPressChooesImage: function() {
    var that = this;
    wx.chooseImage({
      count: 1,
      success: function(res) {
        console.info("选择文件：", res)
        var filePath = res.tempFilePaths[0];

        var formData = {
          'token': 'DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF:vxRImtmQKFYyYgEx4s2qcxhNja8=:eyJzY29wZSI6Inl0Zi1maWxlLXNwYWNlIiwiZGVhZGxpbmUiOjE1NDU4ODM1ODd9'
        };
        wx.uploadFile({
          url: 'https://up-z1.qbox.me',
          // url: 'https://up-z1.qiniup.com',
          filePath: filePath,
          name: 'file',
          formData: formData,
          success: function(res) {
            console.info(res)
          },
          fail: function(e) {
            console.error("错误", e)
          }
        })
      }
    })
  },
  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  }
})