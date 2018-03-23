package com.yunche.loan.config.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class POIUtil {

    private static Logger logger  = Logger.getLogger(POIUtil.class);
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";


    /**
     * 读取EXCEL
     * @param sheetNum
     * @param headLineNum
     * @param filePathName
     * @return
     * @throws IOException
     */
    public static  List<String[]> readExcel(int sheetNum,int headLineNum,String filePathName ) throws IOException {

        File file = new File(filePathName);
        //检查文件  
        checkFile(file);
        //获得Workbook工作薄对象  
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回  
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null){
//            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表  
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                   return null;
                }
                //获得当前sheet的开始行  
                int firstRowNum  = headLineNum;
                //获得当前sheet的结束行  
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行  
                for(int rowNum = firstRowNum;rowNum <= lastRowNum;rowNum++){
                    //获得当前行  
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列  
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数  
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行  
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }

                    list.add(cells);
                }
//            }

        }
        return list;
    }
    public static void checkFile(File file) throws IOException{
        //判断文件是否存在  
        if(null == file){
            logger.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名  
        String fileName = file.getName();
        //判断文件是否是excel文件  
        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
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
            if(fileName.endsWith(xls)){
                //2003  
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(xlsx)){
                //2007  
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return workbook;
    }
    public static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型  
        switch (cell.getCellType()){
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
}