package getMethod;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class Get {
	private static HttpClient httpClient = new HttpClient();

	public static void main(String[] args) throws IOException {

		System.out.println("Get res = " + get("e9857698"));
	}

	public static String get(String id) throws IOException {
		String path = "http://www.medsci.cn/sci/url.do?id=" + id + "&q=w";
		InputStream input = null;
		OutputStream output = null;
		// TODO Auto-generated method stub
		// 得到post方法
		GetMethod getMethod = new GetMethod(path);
		// 设置post方法的参数
		// NameValuePair[] postData = new NameValuePair[1];
		// postData[0] = new NameValuePair("w","java");
		// postMethod.addParameters(postData);

		getMethod.getParams().setParameter("http.protocol.allow-circular-redirects", true);

		// 执行，返回状态码
		int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(getMethod);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("get " + id + " status:" + statusCode);
		// 针对状态码进行处理

		String resURI = null;
		if (statusCode != HttpStatus.SC_INTERNAL_SERVER_ERROR) {
			resURI = getMethod.getURI().toString();
			System.out.println(resURI);

		}
		return resURI;
	}
}
