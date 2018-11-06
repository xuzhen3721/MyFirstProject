package com.xuzhen.main;

import getMethod.Get;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import postMethod.Post;

public class ParserExcel {

	private static Logger log = Logger.getLogger(ParserExcel.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		InputStream is = new FileInputStream("sci.xls");
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

		ParserExcel parserExcel = new ParserExcel();
		parserExcel.readXls(hssfWorkbook);

		FileOutputStream os = new FileOutputStream("sci_res.xls");
		hssfWorkbook.write(os);
		os.close();
	}

	private void readXls(HSSFWorkbook hssfWorkbook) throws IOException {

		// 循环工作表Sheet
		// for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets();
		// numSheet++) {
		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

		String sheetName = hssfSheet.getSheetName();
		log.info(sheetName);

		// 循环行Row
		for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
			System.out.println("-----------------" + rowNum  + " start--------------------------");
			
			HSSFRow hssfRow = hssfSheet.getRow(rowNum);
			if (hssfRow == null) {
				continue;
			}

			String zh_name;
			String pissn;
			HSSFCell urlCell;
			try {
				zh_name = hssfRow.getCell(4).getStringCellValue();
				pissn = hssfRow.getCell(6).getStringCellValue();
				urlCell = hssfRow.getCell(35);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("read excel error", e);
				log.info("sheet=" + sheetName + ",rowNum=" + rowNum);
				break;
			}

			System.out.println("zh_name=" + zh_name + "， pissn=" + pissn
					+ "， time=" + new Date(System.currentTimeMillis()));
			if(pissn==null||pissn.equals("")){
				System.out.println("pissn is null, skip...");
				continue;
			}
			
			
			if (urlCell != null) {
				String url = urlCell.getStringCellValue();
				if (!"".equals(url)) {
					System.out.println("have url: " + url + ", skip...");
					continue;
				}

			}

			String id = Post.post(pissn);
			if(id==null){
				System.out.println("id == null, skip...");
				continue;
			}
			
			String url = Get.get(id);

			HSSFCell cell = hssfRow.createCell(35);
			cell.setCellValue(url);

			
			System.out.println("-----------------" + rowNum  + " end--------------------------\n");
			if (rowNum % 10 == 0) {
				FileOutputStream os = new FileOutputStream("sci_res" + rowNum
						+ ".xls");
				hssfWorkbook.write(os);
				os.close();
			}

		}
		// }
	}

	@SuppressWarnings("static-access")
	private String getValue(HSSFCell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

}