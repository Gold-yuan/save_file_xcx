// page/download/download.js
const {
  listToMatrix,
  always
} = require('../../lib/util.js');
const request = require('../../lib/request.js');

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
    albumList: [],
    domain: domain,
    imgCount: imgCount,
    previewMode: false,
    previewIndex: 0
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
  previewImage(e) {
    
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
  onLoad() {
    updateImageList(this)
    this.renderAlbumList()
  },

  // 渲染相册列表
  renderAlbumList() {
    let layoutColumnSize = this.data.layoutColumnSize;
    let layoutList = [];

    if (this.data.albumList.length) {
      layoutList = listToMatrix([0].concat(this.data.albumList), layoutColumnSize);

      let lastRow = layoutList[layoutList.length - 1];
      if (lastRow.length < layoutColumnSize) {
        let supplement = Array(layoutColumnSize - lastRow.length).fill(0);
        lastRow.push(...supplement);
      }
    }

    this.setData({
      layoutList
    });
  },
  // 退出预览模式
  leavePreviewMode() {
    this.setData({
      previewMode: false,
      previewIndex: 0
    });
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
      var fileList = []
      for (var i = 0; i < data.data.items.length; i++) {
        fileList[i] = domain + data.data.items[i].key
      }
      console.info(fileList)
      that.setData({
        albumList: fileList,
        layoutList: [fileList],
        imgCount: fileList.length
      })
    }
  })
}