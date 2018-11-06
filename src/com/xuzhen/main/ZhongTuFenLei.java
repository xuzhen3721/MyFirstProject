package com.xuzhen.main;

import getMethod.Get;
import getMethod.Get2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import postMethod.Post;

public class ZhongTuFenLei {

	private static Logger log = Logger.getLogger(ZhongTuFenLei.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Map<String, String> FenLeiMap = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String key1, String key2) {

				return key1.compareTo(key2);
			}
		});

		FenLeiMap.put("Q", "生物科学");
		FenLeiMap.put("Q1", "普通生物学");
		FenLeiMap.put("Q2", "细胞学");
		FenLeiMap.put("Q3", "遗传学");
		FenLeiMap.put("Q4", "生理学");
		FenLeiMap.put("Q5", "生物化学");
		FenLeiMap.put("Q6", "生物物理学");
		FenLeiMap.put("Q7", "分子生物学");
		FenLeiMap.put("Q81", "生物工程");
		FenLeiMap.put("Q91", "古生物学");
		FenLeiMap.put("Q93", "微生物学");
		FenLeiMap.put("Q94", "植物学");
		FenLeiMap.put("Q95", "动物学");
		FenLeiMap.put("Q96", "昆虫学");
		FenLeiMap.put("Q98", "人类学");
		FenLeiMap.put("R", "医药、卫生");
		FenLeiMap.put("R1", "预防医学、卫生学");
		FenLeiMap.put("R2", "中国医学");
		FenLeiMap.put("R3", "基础医学");
		FenLeiMap.put("R4", "临床医学");
		FenLeiMap.put("R5", "内科学");
		FenLeiMap.put("R6", "外科学");
		FenLeiMap.put("R71", "妇产科学");
		FenLeiMap.put("R72", "儿科学");
		FenLeiMap.put("R73", "肿瘤学");
		FenLeiMap.put("R74", "神经病学与精神病学");
		FenLeiMap.put("R75", "皮肤病学与性病学");
		FenLeiMap.put("R76", "耳鼻咽喉科学");
		FenLeiMap.put("R77", "眼科学");
		FenLeiMap.put("R78", "口腔科学");
		FenLeiMap.put("R79", "外国民族医学（依世界地区表分）");
		FenLeiMap.put("R8", "特种医学");
		FenLeiMap.put("R9", "药学");

		String prefixUrl = "http://www.socolar.com/pl.aspx?typ=0&ClassCode=";

		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

		Map<String, List> listMap = new HashMap<String, List>();

		for (Map.Entry<String, String> entry : FenLeiMap.entrySet()) {
			String fenLeiCode = entry.getKey();
			String fenLeiStr = entry.getValue();

			String url = prefixUrl + fenLeiCode;

			List<Map<String, String>> list = parserUrl(url);

			if (fenLeiCode.length() > 1) {
				System.out.println("update " + fenLeiCode + "'s Parent Class: " + fenLeiCode.substring(0, 1));
				updateParent(listMap.get(fenLeiCode.substring(0, 1)), list);
			}
			listMap.put(fenLeiCode, list);

		}

		Set<String> set = FenLeiMap.keySet();
		for (String str : set) {
			String fenLeiCode = str;
			List list = listMap.get(fenLeiCode);
			addSheet(hssfWorkbook, fenLeiCode + FenLeiMap.get(fenLeiCode) + "(" + list.size() + ")", list);
		}

		// for (Map.Entry<String, List> entry : listMap.entrySet()) {
		// String fenLeiCode = entry.getKey();
		// List list = entry.getValue();
		// addSheet(hssfWorkbook, fenLeiCode + FenLeiMap.get(fenLeiCode) + "(" + list.size() + ")", list);
		// }

		while (true) {
			try {
				FileOutputStream os = new FileOutputStream("ZhongTuFenLei.xls");
				hssfWorkbook.write(os);
				os.close();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					System.err.println("reTry after 30s...");
					Thread.sleep(30000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
	}

	private static void updateParent(List<Map<String, String>> sumList, List<Map<String, String>> list) {
		int counts = 0;
		for (Map<String, String> map : list) {
			String name = map.get("Name");
			String WebSite = map.get("WebSite");
			String PrintISSN = map.get("PrintISSN");
			String OnlineISSN = map.get("OnlineISSN");
			String Publisher = map.get("Publisher");

			boolean isUpdate = false;
			for (Map<String, String> map2 : sumList) {
				String sumName = map2.get("Name");
				String sumWebSite = map2.get("WebSite");
				String sumPrintISSN = map2.get("PrintISSN");
				String sumOnlineISSN = map2.get("OnlineISSN");
				String sumPublisher = map2.get("Publisher");
				if (name.equals(sumName) && WebSite.equals(sumWebSite) && PrintISSN.equals(sumPrintISSN) && OnlineISSN.equals(sumOnlineISSN)
						&& Publisher.equals(sumPublisher)) {
					map2.put("SecondTitle", map.get("SecondTitle"));
					isUpdate = true;
					counts++;
				}
			}

			if (!isUpdate) {
				// System.err.println("update Parent Failer, data " + map);
				sumList.add(map);
			}
		}
		System.out.println("update Parent counts = " + counts);
		// TODO Auto-generated method stub

	}

	private static List<Map<String, String>> parserUrl(String url) throws IOException {
		List<Map<String, String>> listAll = new ArrayList<Map<String, String>>();
		int pageNo = 1;
		while (true) {
			String splitPageUrl = url + "&Page=" + pageNo;

			List<Map<String, String>> list = Get2.get(splitPageUrl);
			if (list.size() == 0) {
				System.out.println("---------------------------" + url + " END  list size=" + list.size() + "  ------------------------");
				//
				break;
			} else {
				System.out.println("---------------------------" + url + " END, page " + pageNo + " list size=" + list.size()
						+ "  ------------------------");
				//
				listAll.addAll(list);
			}
			pageNo++;

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("---------------------------" + url + " END  listAll size=" + listAll.size() + "  ------------------------");

		if (listAll.size() == 0) {
			try {
				System.err.println("sleep 5s...");
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return parserUrl(url);
		}

		return listAll;
	}

	public static void addSheet(HSSFWorkbook hssfWorkbook, String fenLeiStr, List<Map<String, String>> list) throws IOException {

		// 循环工作表Sheet
		// for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets();
		// numSheet++) {

		HSSFSheet hssfSheet = hssfWorkbook.createSheet(fenLeiStr);

		HSSFRow row = hssfSheet.createRow(0);
		row.createCell(0).setCellValue("FirstTitle");
		row.createCell(1).setCellValue("SecondTitle");
		row.createCell(2).setCellValue("Name");
		row.createCell(3).setCellValue("IsReview");
		row.createCell(4).setCellValue("WebSite");
		row.createCell(5).setCellValue("PrintISSN");
		row.createCell(6).setCellValue("OnlineISSN");
		row.createCell(7).setCellValue("Publisher");

		for (int i = 0; i < list.size(); i++) {
			int rowNum = i + 1;

			Map<String, String> detailMap = list.get(i);

			row = hssfSheet.createRow(rowNum);

			if (detailMap == null) {
				System.err.println("detailMap  is null --------------------------------------------");
				continue;
			}

			if (detailMap.get("FirstTitle") == null) {
				System.err.println("detailMap  is null --------------------------------------------");
				continue;

			}

			row.createCell(0).setCellValue(detailMap.get("FirstTitle"));
			row.createCell(1).setCellValue(detailMap.get("SecondTitle"));
			row.createCell(2).setCellValue(detailMap.get("Name"));
			row.createCell(3).setCellValue(detailMap.get("IsReview"));
			row.createCell(4).setCellValue(detailMap.get("WebSite"));
			row.createCell(5).setCellValue(detailMap.get("PrintISSN"));
			row.createCell(6).setCellValue(detailMap.get("OnlineISSN"));
			row.createCell(7).setCellValue(detailMap.get("Publisher"));
		}

	}

	private static int getInt(String str) {
		int i = 0;
		try {
			Pattern p = Pattern.compile("^\\d+");
			Matcher m = p.matcher(str);
			if (m.find()) {
				i = Integer.valueOf(m.group());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}

}