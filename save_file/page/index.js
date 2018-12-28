// page/download/download.js
var domain = "http://pkducwjmx.bkt.clouddn.com/"
var areaUrl = "https://up-z1.qbox.me"
var fileListUrl = "https://portal.qiniu.com/api/kodo/bucket/files?bucket=savefile&delimiter=&limit=60&marker="

var localFileListUrl = "http://localhost:8080/getFilesByMarker"
var localUploadToken = "http://localhost:8080/getToken"
var imgCount = 0;
Page({
  /**
   * 页面的初始数据
   */
  data: {
    fileList: [],
    domain: domain,
    imgCount: imgCount
  },
  didPressChooesImage: function() {
    var that = this
    wx.chooseImage({
      count: 9,
      success: function(res) {
        console.info("选择文件：", res)
        var filePaths = res.tempFilePaths;
        for (var i = 0; i < res.tempFilePaths.length; i++) {
          uploadFileToQiniu(that, res.tempFilePaths[i]);
        }
        setTimeout(function() {
          updateImageList(that)
        }, 1111)

      }
    })
  },

  onLoad() {
    updateImageList(this)
  }
})

function uploadFileToQiniu(that, filePath) {
  // 上传
  var formData = {};
  wx.request({
    url: localUploadToken,
    success: function(data) {
      formData.token = data.data
      console.info(formData)
      wx.uploadFile({
        url: areaUrl,
        // url: 'https://up-z1.qiniup.com',
        filePath: filePath,
        name: 'file',
        formData: formData,
        success: function(res) {
          console.info("上传结果", res)
        },
        fail: function(e) {
          console.error("错误", e)
        }
      })
    }
  })

}

function updateImageList(that) {
  wx.request({
    url: localFileListUrl,
    success: function(data) {
      console.info(data.data)
      that.setData({
        fileList: data.data.items,
        imgCount: data.data.items.length
      })
    }
  })
}