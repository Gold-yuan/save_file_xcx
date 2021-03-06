package com.ytf.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;

public class UploadFileTest {
	public final static String UPLOAD_LIST_URL = "https://uc.qbox.me/v1/query?ak=DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF&bucket=ytf-file-space";
	// "https://up-z1.qbox.me",
	// "http://up-z1.qiniu.com",
	public final static String UPLOAD_SERVICE_URL = "https://up-z1.qbox.me";
	public final static String DOWNLOAD_SERVICE_URL = "http://pk2skjaii.bkt.clouddn.com/";
	public final static String BUCKET_NAME = "savefile";
	// public final static String BUCKET_NAME = "ytf-file-space";

	// 文件上传空间授权 2018-12-22，1个月
	public String upToken = "DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF:Ed9XqbDKvbgp7Gevyg1VTkW7QSY=:eyJzY29wZSI6Inl0Zi1maWxlLXNwYWNlIiwiZGVhZGxpbmUiOjE1NDUzODYwNjR9";

	Auth auth;
	UploadManager uploadManager;
	String uploadToken; // 授权上传的token
	String downloadUrl; // 授权下载的文件url
	String baseUrl;
	String picName;
	BucketManager bucketManager;

	@Before
	public void init() throws Exception {
		auth = Auth.create("DZ-nhG6NJvXfeSAOF1dlLerSP-qmmulMUGB3OgRF", "4n7cD9XMMQNg6aw16HqEkAH_uPYQiKZrkvQoAgI3");
		picName = "头像.jpg";
		// 上传
		uploadToken = auth.uploadToken(BUCKET_NAME);
		System.out.println("uploadToken: " + uploadToken);
		Zone z = Zone.huabei();
		Configuration config = new Configuration(z);
		uploadManager = new UploadManager(config);

		// 下载
		baseUrl = DOWNLOAD_SERVICE_URL + URLEncoder.encode(picName, "utf-8");

		// 管理，实例化一个BucketManager对象
		bucketManager = new BucketManager(auth, config);
	}

	@Test
	public void uploadFile() throws QiniuException {
		File file = new File("C:\\Users\\IBM_ADMIN\\Desktop\\soft\\新建文件夹\\头像.jpg");
		auth.uploadToken(BUCKET_NAME);
		Response r = uploadManager.put(this.getBytes(file.getAbsolutePath()), picName, uploadToken);
		System.out.println("返回结果：" + r.getInfo());
	}

	@Test
	public void downloadFile() throws UnsupportedEncodingException {
		downloadUrl = auth.privateDownloadUrl(baseUrl);
		System.out.println(downloadUrl);
	}

	@Test
	public void removeFile() throws Exception {
		// 调用delete方法移动文件
		Response delete = bucketManager.delete(BUCKET_NAME, picName);
		System.out.println(delete);
	}

	@Test
	public void getFileList() throws Exception {
		// 文件名前缀
		String prefix = "";
		// 每次迭代的长度限制，最大1000，推荐值 1000
		int limit = 50;
		// 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
		String delimiter = "";

		// 列举空间文件列表
		BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(BUCKET_NAME, prefix,
				limit, delimiter);
		while (fileListIterator.hasNext()) {
			// 处理获取的file list结果
			FileInfo[] items = fileListIterator.next();
			for (FileInfo item : items) {
				System.out.println(item.key);
				// System.out.println(item.hash);
				// System.out.println(item.fsize);
				// System.out.println(item.mimeType);
				// System.out.println(item.putTime);
				// System.out.println(item.endUser);
			}
		}
	}

	/**
	 * 分页查询
	 */
	@Test
	public void getFileListMarker() throws Exception {
		// 文件名前缀
		String prefix = "";
		// 每次迭代的长度限制，最大1000，推荐值 1000
		int limit = 10;
		// 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
		String delimiter = "";

		List<FileInfo> files = new ArrayList<>();
		FileListing listFiles = new FileListing();
		do {
			listFiles = bucketManager.listFilesV2(BUCKET_NAME, prefix, listFiles.marker, limit, delimiter);
			files.addAll(Arrays.asList(listFiles.items));

			System.out.println(files.size());
			System.out.println(listFiles.marker);
		} while (!listFiles.isEOF());

	}

	@Test
	public void domainList() throws Exception {

		String[] domainLists = bucketManager.domainList(BUCKET_NAME);
		for (String domain : domainLists)
			System.out.print(domain);
		System.out.println(domainLists.length);
	}

	private byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static void main(String[] args) {

	}
}
