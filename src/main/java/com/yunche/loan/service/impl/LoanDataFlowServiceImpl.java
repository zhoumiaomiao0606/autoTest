package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.DepartmentCache;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanDataFlowDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.DictService;
import com.yunche.loan.service.LoanDataFlowService;
import com.yunche.loan.service.TaskSchedulingService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.yunche.loan.config.constant.ExportExcelConst.*;
import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMddHHmmss;
import static com.yunche.loan.service.impl.VideoFaceServiceImpl.autoSizeColumn;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
@Service
public class LoanDataFlowServiceImpl implements LoanDataFlowService {

    private static final Logger logger = LoggerFactory.getLogger(LoanDataFlowServiceImpl.class);

    @Autowired
    private LoanDataFlowDOMapper loanDataFlowDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private DepartmentCache departmentCache;

    @Autowired
    private DictService dictService;


    @Override
    public LoanDataFlowDO getLastByOrderIdAndType(Long orderId, Byte type) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");
        Preconditions.checkNotNull(type, "type不能为空");

        LoanDataFlowDO loanDataFlowDO = loanDataFlowDOMapper.getLastByOrderIdAndType(orderId, type);
        return loanDataFlowDO;
    }

    @Override
    public ResultBean<UniversalDataFlowDetailVO> detail(Long id) {
        Preconditions.checkNotNull(id, "资料流转单ID不能为空");

        UniversalDataFlowDetailVO universalDataFlowDetailVO = loanQueryDOMapper.selectUniversalDataFlowDetail(id);

        // tyoe - kvMap
        Map<String, String> loanDataFlowTypeKVMap = dictService.getKVMap("loanDataFlowType");
        universalDataFlowDetailVO.setTypeText(loanDataFlowTypeKVMap.get(String.valueOf(universalDataFlowDetailVO.getType())));

        // expressCom - kvMap
        Map<String, String> loanDataFlowExpressComKVMap = dictService.getKVMap("loanDataFlowExpressCom");
        universalDataFlowDetailVO.setExpressComText(loanDataFlowExpressComKVMap.get(String.valueOf(universalDataFlowDetailVO.getExpressCom())));

        return ResultBean.ofSuccess(universalDataFlowDetailVO);
    }

    @Override
    @Transactional
    public ResultBean create(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getType(), "type不能为空");

        loanDataFlowDO.setGmtCreate(new Date());
        loanDataFlowDO.setGmtModify(new Date());
        int count = loanDataFlowDOMapper.insertSelective(loanDataFlowDO);
        Preconditions.checkArgument(count > 0, "插入失败");

        return ResultBean.ofSuccess(loanDataFlowDO.getId(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean update(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getId(), "id不能为空");

        loanDataFlowDO.setGmtModify(new Date());
        int count = loanDataFlowDOMapper.updateByPrimaryKeySelective(loanDataFlowDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<List<BaseVO>> flowDept() {
        List<BaseVO> flowDept = departmentCache.getFlowDept();
        return ResultBean.ofSuccess(flowDept);
    }

    @Override
    public ResultBean<List<UniversalCustomerOrderVO>> queryDataFlowCustomerOrder(String customerName) {

        // telephone_verify = 1  &&   data_flow_mortgage_b2c = 0
        Long loginUserId = SessionUtils.getLoginUser().getId();
        List<UniversalCustomerOrderVO> universalCustomerOrderVOS = loanQueryDOMapper.selectUniversalDataFlowCustomerOrder(loginUserId, customerName);

        return ResultBean.ofSuccess(universalCustomerOrderVOS);
    }

    @Override
    public ResultBean<String> export(TaskListQuery taskListQuery) {
        Preconditions.checkNotNull(taskListQuery.getTaskDefinitionKey(), "taskKey不能为空");
        Preconditions.checkNotNull(taskListQuery.getDataFlowType(), "资料流转类型不能为空");

        // 最大导出量：2000条
        taskListQuery.setPageIndex(1);
        taskListQuery.setPageSize(2000);

        // 需要导出的数据
        ResultBean<List<TaskListVO>> listResultBean = taskSchedulingService.queryTaskList(taskListQuery);
        Preconditions.checkArgument(listResultBean.getSuccess(), listResultBean.getMsg());

        List<TaskListVO> data = listResultBean.getData();

        // now
        String now = LocalDateTime.now().format(formatter_yyyyMMddHHmmss);

        // 文件名
        String exportFileName = "资料流转_" + now + ".xlsx";
        String[] cellTitle = {"业务编号", "姓名", "身份证号码", "业务团队", "资料流转类型",
                "寄送公司", "寄送单号", "寄送日期（格式：2018-08-08 ）", "接收日期（格式：2018-08-08 ）", "接收人",
                "含抵押资料（是/否）", "流出部门", "流入部门"};

        // 声明一个工作薄
        XSSFWorkbook workBook = new XSSFWorkbook();

        // 生成一个表格
        XSSFSheet sheet = workBook.createSheet();

        // sheet name
        workBook.setSheetName(0, "资料流转");

        // 创建表格标题行  第一行
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < cellTitle.length; i++) {
            // 列
            titleRow.createCell(i).setCellValue(cellTitle[i]);
        }

        // 插入需导出的数据
        for (int i = 0; i < data.size(); i++) {

            // 创建行
            XSSFRow row = sheet.createRow(i + 1);

            // 行数据
            TaskListVO taskListVO = data.get(i);

            // 列
            row.createCell(0).setCellValue(taskListVO.getId());
            row.createCell(1).setCellValue(taskListVO.getCustomer());
            row.createCell(2).setCellValue(taskListVO.getIdCard());
            row.createCell(3).setCellValue(taskListVO.getPartner());
            row.createCell(4).setCellValue(taskListVO.getDataFlowTypeText());
        }

        // 自动调整列宽
        autoSizeColumn(sheet, cellTitle.length);

        // file
        File file = new File("/tmp/" + exportFileName);

        FileOutputStream os = null;
        try {
            // 文件输出流
            os = new FileOutputStream(file);
            workBook.write(os);

        } catch (FileNotFoundException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            try {
                os.flush();

            } catch (IOException e) {
                logger.error("", e);
            }

            try {
                os.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        // 上传到OSS
        OSSUnit.uploadObject2OSS(OSSUnit.getOSSClient(), file, BUCKET_NAME_VIDEO_FACE, OSS_DISK_NAME_DATA_FLOW);

        return ResultBean.ofSuccess(OSS_DISK_NAME_DATA_FLOW + exportFileName);
    }

    @Override
    public ResultBean<Integer> imp(String ossKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(ossKey), "ossKey不能为空");

        // 收集数据
        List<LoanDataFlowDO> loanDataFlowDOList = Lists.newArrayList();

        try {
            // readFile
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 0, ossKey);

            if (!CollectionUtils.isEmpty(rowList)) {

                // 标题行
                String[] titleRow = rowList.get(0);

                // vkMap
                Map<String, String> type_VKMap = dictService.getVKMap("loanDataFlowType");
                Map<String, String> expressCom_VKMap = dictService.getVKMap("loanDataFlowExpressCom");

                for (int i = 1; i < rowList.size(); i++) {

                    // 当前行数
                    int rowNum = i + 1;

                    // 当前行数据
                    String[] row = rowList.get(i);

                    // 空行跳过
                    if (ArrayUtils.isEmpty(row)) {
                        continue;
                    }

                    // 空位补""
                    if (row.length < titleRow.length) {

                        int len = row.length;

                        // 扩容
                        row = Arrays.copyOf(row, titleRow.length);

                        for (int k = len; k < titleRow.length; k++) {
                            row[k] = "";
                        }
                    }

                    LoanDataFlowDO loanDataFlowDO = new LoanDataFlowDO();

                    try {
                        loanDataFlowDO.setOrderId(Long.valueOf(row[0]));
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第1列格式有误：" + row[0]);
                    }

                    try {
                        String row_4 = row[4].trim();
                        Preconditions.checkArgument(StringUtils.isNotBlank(row_4), "第" + rowNum + "行，第5列格式有误：[" + titleRow[4] + "]不能为空");

                        String type = type_VKMap.get(row_4);
                        Preconditions.checkArgument(StringUtils.isNotBlank(type), "第" + rowNum + "行，第5列格式有误！无对应[资料流转类型]：" + row_4);

                        loanDataFlowDO.setType(Byte.valueOf(type));

                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第5列格式有误：" + row[4]);
                    }

                    try {
                        String row_5 = row[5].trim();
                        Preconditions.checkArgument(StringUtils.isNotBlank(row_5), "第" + rowNum + "行，第5列格式有误：[" + titleRow[5] + "]不能为空");

                        String expressCom = expressCom_VKMap.get(row_5);
                        Preconditions.checkArgument(StringUtils.isNotBlank(expressCom), "第" + rowNum + "行，第6列格式有误！无对应[寄送公司]：" + row_5);

                        loanDataFlowDO.setExpressCom(Byte.valueOf(expressCom));

                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第6列格式有误：" + row[5]);
                    }

                    try {
                        String row_6 = row[6].trim();
                        Preconditions.checkArgument(StringUtils.isNotBlank(row_6), "第" + rowNum + "行，第7列格式有误：[" + titleRow[6] + "]不能为空");

                        loanDataFlowDO.setExpressNum(row_6);

                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第7列格式有误：" + row[6]);
                    }

                    try {
                        String row_7 = row[7].trim();
                        Preconditions.checkArgument(StringUtils.isNotBlank(row_7), "第" + rowNum + "行，第8列格式有误：[" + titleRow[7] + "]不能为空");

                        loanDataFlowDO.setExpressSendDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(row_7));

                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第8列格式有误：" + row[7]);
                    }

                    try {
                        loanDataFlowDO.setExpressReceiveDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(row[8]));
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第9列格式有误：" + row[8]);
                    }

                    try {
                        loanDataFlowDO.setExpressReceiveMan(row[9]);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第10列格式有误：" + row[9]);
                    }

                    try {
                        String row_10 = row[10].trim();
                        Preconditions.checkArgument(StringUtils.isNotBlank(row_10), "第" + rowNum + "行，第11列格式有误：[" + titleRow[10] + "]不能为空");

                        loanDataFlowDO.setHasMortgageContract(convertHasMortgageContract(row_10));

                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第11列格式有误：" + row[10]);
                    }

                    try {
                        loanDataFlowDO.setFlowOutDeptName(row[11]);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第12列格式有误：" + row[11]);
                    }

                    try {
                        loanDataFlowDO.setFlowInDeptName(row[12]);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第13列格式有误：" + row[12]);
                    }

                    loanDataFlowDO.setGmtCreate(new Date());
                    loanDataFlowDO.setGmtModify(new Date());

                    loanDataFlowDOList.add(loanDataFlowDO);
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // batchInsert
        int count = batchInsert(loanDataFlowDOList);

        return ResultBean.ofSuccess(count, "导入成功");
    }

    /**
     * 批量导入
     *
     * @param loanDataFlowDOList
     * @return
     */

    private int batchInsert(List<LoanDataFlowDO> loanDataFlowDOList) {

        if (CollectionUtils.isEmpty(loanDataFlowDOList)) {
            return 0;
        }

        // batchInsert
        int count = loanDataFlowDOMapper.batchInsert(loanDataFlowDOList);
        return count;
    }

    private Byte convertHasMortgageContract(String field) {

        if ("否".equals(field)) {
            return 0;
        } else if ("是".equals(field)) {
            return 1;
        }

        return null;
    }

}