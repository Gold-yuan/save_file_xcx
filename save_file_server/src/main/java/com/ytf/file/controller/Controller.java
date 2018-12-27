package com.ytf.file.controller;

import java.net.URLEncoder;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonParser;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;

@RestController
public class Controller {

	@RequestMapping("/getToken")
	public String getToken() throws QiniuException {
		Auth auth = Auth.create("DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF", "4n7cD9XMMQNg6aw16HqEkAH_uPYQiKZrkvQoAgI3");
		String token = auth.uploadToken("ytf-file-space");
		return token;
	}

	public final static String DOWNLOAD_SERVICE_URL = "http://pk2skjaii.bkt.clouddn.com/";

	@RequestMapping("/getDownloadUrl")
	public String getDownloadUrl(String picName) throws Exception {
		System.out.println(picName);
		if (StringUtils.isEmpty(picName)) {
			return "500";
		}
		Auth auth = Auth.create("DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF", "4n7cD9XMMQNg6aw16HqEkAH_uPYQiKZrkvQoAgI3");
		String baseUrl = DOWNLOAD_SERVICE_URL + URLEncoder.encode(picName, "utf-8");
		String downloadUrl = auth.privateDownloadUrl(baseUrl);
		return downloadUrl;
	}
	@RequestMapping("/removeFile")
	public String removeFile(String picName) throws Exception {
		Auth auth = Auth.create("DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF", "4n7cD9XMMQNg6aw16HqEkAH_uPYQiKZrkvQoAgI3");
		Zone z = Zone.huabei();
		Configuration config = new Configuration(z);

		// 管理，实例化一个BucketManager对象
		BucketManager bucketManager = new BucketManager(auth, config);
		// 调用delete方法移动文件
		Response delete = bucketManager.delete("ytf-file-space", picName);
		System.out.println(delete);
		return delete.statusCode +"";
	}

	public static void main(String[] args) {
		Auth auth = Auth.create("DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF", "4n7cD9XMMQNg6aw16HqEkAH_uPYQiKZrkvQoAgI3");
		String token = auth.uploadToken("ytf-file-space");
		auth.uploadToken("ytf-file-space", null, 3600 * 24 * 30, null);
		System.out.println(token);
	}
}
