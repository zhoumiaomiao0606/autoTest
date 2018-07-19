package com.yunche.loan.config.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class POIUtil {

    private static Logger logger = Logger.getLogger(POIUtil.class);
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";


    /**
     * 读取EXCEL
     *
     * @param sheetNum
     * @param headLineNum
     * @param filePathName
     * @return
     * @throws IOException
     */
    public static List<String[]> readExcel(int sheetNum, int headLineNum, String filePathName) throws IOException {

        File file = new File(filePathName);
        //检查文件  
        checkFile(file);
        //获得Workbook工作薄对象  
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回  
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
//            for(int sheetNo = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
            //获得当前sheet工作表
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if (sheet == null) {
                return null;
            }
            //获得当前sheet的开始行
            int firstRowNum = headLineNum;
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            //循环除了第一行的所有行
            for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                //获得当前行
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                //获得当前行的开始列
                int firstCellNum = row.getFirstCellNum();
                //获得当前行的列数
                int lastCellNum = row.getPhysicalNumberOfCells();
                String[] cells = new String[row.getPhysicalNumberOfCells()];
                //循环当前行
                for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    cells[cellNum] = getCellValue(cell);
                }

                list.add(cells);
            }
//            }

        }
        return list;
    }

    /**
     * 读取EXCEL
     *
     * @param sheetNum
     * @param headLineNum 开始行
     * @param key
     * @return
     * @throws IOException
     */
    public static List<String[]> readExcelFromOSS(int sheetNum, int headLineNum, String key) throws IOException {

//        File file = new File(filePathName);
        //检查文件
//       checkFile(file);

        if (!key.endsWith(xls) && !key.endsWith(xlsx)) {
            logger.error(key + "不是excel文件");
            throw new IOException(key + "不是excel文件");
        }

        //获得Workbook工作薄对象
        InputStream is = OSSUnit.getOSS2InputStream(key);
//        InputStream is = OSSUnit.getOSSClient().getObject("yunche-2018", key).getObjectContent();
//        Workbook workbook = getWorkBook(file);
        Workbook workbook = getWorkBookOSS(is, key);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
//            for(int sheetNo = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
            //获得当前sheet工作表
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if (sheet == null) {
                return null;
            }
            //获得当前sheet的开始行
            int firstRowNum = headLineNum;
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            //循环除了第一行的所有行
            for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                //获得当前行
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                //获得当前行的开始列
                int firstCellNum = row.getFirstCellNum();
                //获得当前行的列数
                short lastCellNum1 = row.getLastCellNum();
                int lastCellNum = row.getPhysicalNumberOfCells();
                String[] cells = new String[lastCellNum1];
                //循环当前行
                for (int cellNum = firstCellNum; cellNum < lastCellNum1; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    cells[cellNum] = getCellValue(cell);
                }

                list.add(cells);
            }
//            }

        }
        return list;
    }

    public static void checkFile(File file) throws IOException {
        //判断文件是否存在  
        if (null == file) {
            logger.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名  
        String fileName = file.getName();
        //判断文件是否是excel文件  
        if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {
            logger.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }

    public static Workbook getWorkBook(File file) {
        //获得文件名  
        String fileName = file.getName();
        //创建Workbook工作薄对象，表示整个excel  
        Workbook workbook = null;
        try {
            //获取excel文件的io流  
            InputStream is = new FileInputStream(file) {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象  
            if (fileName.endsWith(xls)) {
                //2003  
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(xlsx)) {
                //2007  
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return workbook;
    }

    public static Workbook getWorkBookOSS(InputStream is, String key) {

        Workbook workbook = null;
        try {
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (key.endsWith(xls)) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (key.endsWith(xlsx)) {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况  
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型  
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    /**
     * 自动调整列宽
     *
     * @param sheet
     * @param columnLength
     */
    public static void autoSizeColumn(XSSFSheet sheet, int columnLength) {
        for (int i = 0; i < columnLength; i++) {
            sheet.autoSizeColumn(i, true);
        }
    }

    /**
     * 单元格格式：文本
     *
     * @param workBook
     * @param sheet
     * @param columnLength
     */
    public static void textStyle(XSSFWorkbook workBook, XSSFSheet sheet, int columnLength) {

        // 样式
        CellStyle cellStyle = workBook.createCellStyle();

//        // 水平居中
//        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//        // 垂直居中
//        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

        // 单元格格式
        DataFormat format = workBook.createDataFormat();
        // 设置文本格式
        cellStyle.setDataFormat(format.getFormat("@"));

        for (int i = 0; i < columnLength; i++) {
            sheet.setDefaultColumnStyle(i, cellStyle);
        }
    }

//    /**
//     * 往excel中写入.
//     * @param filePath    文件路径
//     * @param sheetName  表格索引
//     * @param object
//     */
//    public void writeToExcel(String filePath,String sheetName, Object object,List list){
//        //创建workbook
//        File file = new File(filePath);
//
//        Workbook workBook = getWorkBook(file);
//        Sheet sheet = workBook.createSheet("sheet1");
//
//
//        for(int i=0;i<list.size();i++){
//            Row row = sheet.createRow(i);
//
//            Cell cell = row.createCell(i);
//            cell.setCellValue();
//
//
//        }
//
//        FileOutputStream out = null;
//        Sheet sheet = workBook.getSheet(sheetName);
//        // 获取表格的总行数
//        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
//        try {
//            Row row = sheet.createRow(rowCount);     //最新要添加的一行
//            //通过反射获得object的字段,对应表头插入
//            // 获取该对象的class对象
//            Class<? extends Object> class_ = object.getClass();
//
//            for(int i = 0;i < titleRow.length;i++){
//                String title = titleRow[i];
//                String UTitle = Character.toUpperCase(title.charAt(0))+ title.substring(1, title.length()); // 使其首字母大写;
//                String methodName  = "get"+UTitle;
//                Method method = class_.getDeclaredMethod(methodName); // 设置要执行的方法
//                String data = method.invoke(object).toString(); // 执行该get方法,即要插入的数据
//                Cell cell = row.createCell(i);
//                cell.setCellValue(data);
//            }
//            out = new FileOutputStream(filePath);
//            workbook.write(out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
