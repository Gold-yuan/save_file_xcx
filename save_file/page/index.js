// page/download/download.js
var domain = "http://pkducwjmx.bkt.clouddn.com/"
var areaUrl = "https://up-z1.qbox.me"
var fileListUrl = "https://portal.qiniu.com/api/kodo/bucket/files?bucket=savefile&delimiter=&limit=60&marker="

var localFileListUrl = "http://localhost:8080/getFilesByMarker"
var localUploadToken = "http://localhost:8080/getToken"
var localRemoveFile = "http://localhost:8080/removeFile"
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
        if (filePaths.length > 0) {
          that.showLoading('loading')
        }
        for (var i = 0; i < filePaths.length; i++) {
          uploadFileToQiniu(that, filePaths[i]);
        }
        setTimeout(function () {
          updateImageList(that)
        }, 1111)

      }
    })
  },
  previewImage(e) {
    if (this.data.showActionsSheet) {
      return
    }
    var index = e.currentTarget.dataset.index;
    var imgArr = this.data.fileList;
    console.info(imgArr)
    wx.previewImage({
      current: imgArr[index], //当前图片地址 
      urls: imgArr, //所有要预览的图片的地址集合 数组形式 
      success: function(res) {},
      fail: function(res) {},
      complete: function(res) {},
    })

  },

  // 显示toast消息
  showToast(toastMessage) {
    this.setData({
      showToast: true,
      toastMessage
    });
  },
  // 隐藏toast消息
  hideToast() {
    this.setData({
      showToast: false,
      toastMessage: ''
    });
  },
  // 显示loading提示
  showLoading(loadingMessage) {
    this.setData({
      showLoading: true,
      loadingMessage
    });
  },

  // 隐藏loading提示
  hideLoading() {
    this.setData({
      showLoading: false,
      loadingMessage: ''
    });
  },
  // 隐藏动作列表
  hideActionSheet() {
    this.setData({
      showActionsSheet: false,
      imageInAction: ''
    })
  },
  // 显示可操作命令
  showActions(event) {
    this.setData({
      showActionsSheet: true,
      imageInAction: event.target.dataset.src
    });
  },
  deleteImage(){
    wx.request({
      url: localRemoveFile,
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
          that.showToast('上传成功')
        },
        fail: function(e) {
          console.error("错误", e)
        }
      })
    }
  })

}

function updateImageList(that) {
  that.showLoading('loading')
  wx.request({
    url: localFileListUrl,
    success: function(data) {
      var fileList = []
      for (var i = 0; i < data.data.items.length; i++) {
        fileList[i] = domain + data.data.items[i].key
      }
      that.setData({
        fileList: fileList,
        imgCount: fileList.length
      })
      that.hideLoading()
    }
  })
}