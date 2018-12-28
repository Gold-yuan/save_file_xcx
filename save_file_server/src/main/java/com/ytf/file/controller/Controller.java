package com.ytf.file.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;

@RestController
public class Controller {
	// public final static String DOWNLOAD_SERVICE_URL =
	// "http://pk2skjaii.bkt.clouddn.com/";
	// public final static String BUCKET = "ytf-file-space";
	public final static String DOWNLOAD_SERVICE_URL = "http://pkducwjmx.bkt.clouddn.com/";
	public final static String BUCKET = "savefile";
	public final static String ACCESS_KEY = "DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF";
	public final static String SECRET_KEY = "4n7cD9XMMQNg6aw16HqEkAH_uPYQiKZrkvQoAgI3";

	public static Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
	public static Zone z = Zone.huabei();
	public static Configuration config = new Configuration(z);

	@RequestMapping("/getToken")
	public String getToken(@RequestParam(required = false, defaultValue = "3600") long expires) throws QiniuException {
		String token = auth.uploadToken(BUCKET);
		return token;
	}

	@RequestMapping("/getDownloadUrl")
	public String getDownloadUrl(String picName) throws Exception {
		if (StringUtils.isEmpty(picName)) {
			return "error";
		}
		String baseUrl = DOWNLOAD_SERVICE_URL + URLEncoder.encode(picName, "utf-8");
		String downloadUrl = auth.privateDownloadUrl(baseUrl);
		return downloadUrl;
	}

	@RequestMapping("/removeFile")
	public String removeFile(String picName) throws Exception {
		// 管理，实例化一个BucketManager对象
		BucketManager bucketManager = new BucketManager(auth, config);
		// 调用delete方法移动文件
		Response delete = bucketManager.delete(BUCKET, picName);
		System.out.println(delete);
		return delete.statusCode + "";
	}

	@RequestMapping("/getFilesByMarker")
	public FileListing getFilesByMarker(@RequestParam(required = false, defaultValue = "") String prefix,
			@RequestParam(required = false) String marker) throws Exception {
		// 管理，实例化一个BucketManager对象
		BucketManager bucketManager = new BucketManager(auth, config);
		// 文件名前缀 prefix
		// 每次迭代的长度限制，最大1000，推荐值 1000
		int limit = 100;
		// 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
		String delimiter = "";
		FileListing fileListing = bucketManager.listFiles(BUCKET, null, null, limit, delimiter);
		
		return fileListing;
	}

	@RequestMapping("/getFiles")
	public List<FileInfo> getFileList(@RequestParam(required = false, defaultValue = "") String prefix)
			throws Exception {
		// 管理，实例化一个BucketManager对象
		BucketManager bucketManager = new BucketManager(auth, config);
		// 文件名前缀 prefix
		// 每次迭代的长度限制，最大1000，推荐值 1000
		int limit = 1000;
		// 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
		String delimiter = "";

		List<FileInfo> files = new ArrayList<>();
		// 列举空间文件列表
		BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(BUCKET, prefix, limit,
				delimiter);

		while (fileListIterator.hasNext()) {
			// 处理获取的file list结果
			FileInfo[] items = fileListIterator.next();
			files.addAll(Arrays.asList(items));
		}
		Collections.sort(files, new Comparator<FileInfo>() {

			@Override
			public int compare(FileInfo o1, FileInfo o2) {
				if (o1.putTime > o2.putTime) {
					return -1;
				}
				return 0;
			}
		});

		return files;
	}
}
