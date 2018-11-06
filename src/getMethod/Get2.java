package getMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

public class Get2 {
	private static HttpClient httpClient = new HttpClient();

	public static void main(String[] args) throws IOException {

		System.out.println("Get res = " + get("http://www.socolar.com/pl.aspx?typ=0&ClassCode=R79"));
	}

	public static List<Map<String, String>> get(String url) throws IOException {
		InputStream input = null;
		OutputStream output = null;

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// 得到post方法
		GetMethod getMethod = new GetMethod(url);
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
		System.out.println("http status:" + statusCode);
		// 针对状态码进行处理

		if (statusCode == HttpStatus.SC_OK) {
			String body = getMethod.getResponseBodyAsString();
			// System.out.println(body);
			body = body.replaceAll("\r|\n", "");
			// System.out.println(body);
			Pattern pattern = Pattern.compile("<div style=\"margin-left:10px\">(.+?)</div>");
			Matcher matcher = pattern.matcher(body);

			// <span id="lblTt" class="TtBig"><a href='pl.aspx?ClassCode=Q&CName=%e7%94%9f%e7%89%a9%e7%a7%91%e5%ad%a6'>生物科学</a>")
			String FirstTitle = getValue(body, "<span id=\"lblTt\" class=\"TtBig\"><a href='.*?'>(.+?)</a>");
			// System.out.println("FirstTitle=" + FirstTitle.trim());
			String SecondTitle = getValue(body, "» <a href='.*?'>(.{2,15})</a></span>");
			// System.out.println("SecondTitle=" + SecondTitle.trim());

			while (matcher.find()) {
				String detail = matcher.group(1);
				detail = detail.replaceAll("&nbsp;", "");
				// System.out.println("detail= " + detail);
				String Name = getValue(detail, "<span class=\"Tt\">(.+?)</span>");
				String IsReview = getValue(detail, "<span class=pp>(.+?)</span>");
				String WebSite = getValue(detail, "<a target=_blank href=\"(.+?)\">");
				WebSite = URLDecoder.decode(WebSite.trim()).replaceAll("vt\\.aspx\\?url=", "");// URL解码

				String PrintISSN = getValue(detail, "Print ISSN :([\\w]{4}-[\\w]{4})");
				String OnlineISSN = getValue(detail, "Online ISSN :(.+?)<br>");
				String Publisher = getValue(detail, "<br>Publisher:(.+?)<br>");

				// System.out.println("Name=" + Name.trim());
				// System.out.println("IsReview=" + IsReview.trim());
				// System.out.println("WebSite=" + WebSite.trim());
				// System.out.println("PrintISSN=" + PrintISSN.trim());
				// System.out.println("OnlineISSN=" + OnlineISSN.trim());
				// System.out.println("Publisher=" + Publisher.trim());

				Map<String, String> map = new HashMap<String, String>();
				map.put("FirstTitle", FirstTitle.trim());
				map.put("SecondTitle", SecondTitle.trim());
				map.put("Name", Name.trim());
				map.put("IsReview", IsReview.trim());

				map.put("WebSite", WebSite.trim());
				map.put("PrintISSN", PrintISSN.trim());
				map.put("OnlineISSN", OnlineISSN.trim());
				map.put("Publisher", Publisher.trim());
				if (WebSite != null && !"".equals(WebSite.trim()))
					list.add(map);

			}

			// ""<div style="margin-left:10px">
			// <img src="pic/small_bullet.gif" align=texttop>&nbsp; <a href='vn.aspx?id=9679'>
			// <span class="Tt">
			// Abstracts Issue / Biophysical Society
			// </span></a>
			// &nbsp;&nbsp;<a target=_blank href="vt.aspx?url=http%3a%2f%2fwww.biophysics.org%2ftabid%2f461%2fdefault.aspx"><img
			// src='pic/link.gif'>
			// Visit Web site</a>
			// <br>&nbsp;&nbsp;&nbsp;
			// <br>&nbsp;&nbsp;&nbsp;Publisher:Biophysical Society<br>&nbsp;

			return list;
		}
		return list;
	}

	private static String getValue(String detail, String patternStr) {

		try {
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(detail);

			while (matcher.find()) {
				String val = matcher.group(1);
				return val;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
