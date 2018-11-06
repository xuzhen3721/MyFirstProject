package postMethod;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class Post {
	/**
	 * @param args
	 */
	private static HttpClient httpClient = new HttpClient();

	public static void main(String[] args) throws IOException {
		System.out.println("post res = " + post("1932-6203"));

	}

	public static String post(String pissn) throws IOException {
		String path = "http://www.medsci.cn/sci/index.do?action=search#result";
		InputStream input = null;
		OutputStream output = null;
		// TODO Auto-generated method stub
		// 得到post方法
		PostMethod postMethod = new PostMethod(path);
		// 设置post方法的参数
		NameValuePair[] postData = new NameValuePair[1];
		postData[0] = new NameValuePair("fullname", pissn);

		postMethod.addParameters(postData);

		// 执行，返回状态码
		int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("post" + pissn + " status:" + statusCode);
		// 针对状态码进行处理

		String id = null;
		if (statusCode == HttpStatus.SC_OK) {
			String body = postMethod.getResponseBodyAsString();
			// System.out.println(body);

			// <td target="_blank" data-title="主页"><a href="journal.do?id=e9857698"

			// Pattern pattern = Pattern.compile("(?:(\\d+))?\\s?([a-zA-Z]+)?.+");
			// Pattern pattern = Pattern.compile(".*journal\\.do\\?id=(\\w*).*");

			Pattern pattern = Pattern.compile("<td target=\"_blank\" data-title=\"主页\"><a href=\"journal.do\\?id=(.+?)\"");
			Matcher matcher = pattern.matcher(body);

			while (matcher.find()) {
				id = matcher.group(1);
				System.out.println("id= " + id);
			}

		}
		return id;
	}
}