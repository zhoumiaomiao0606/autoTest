package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.VideoFaceConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.BankRelaQuestionDO;
import com.yunche.loan.domain.entity.VideoFaceLogDO;
import com.yunche.loan.domain.query.VideoFaceQuery;
import com.yunche.loan.domain.vo.VideoFaceLogVO;
import com.yunche.loan.mapper.BankRelaQuestionDOMapper;
import com.yunche.loan.mapper.VideoFaceLogDOMapper;
import com.yunche.loan.service.CarService;
import com.yunche.loan.service.VideoFaceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.CarConst.CAR_MODEL;
import static com.yunche.loan.config.constant.VideoFaceConst.GUARANTEE_COMPANY_ID;
import static com.yunche.loan.config.constant.VideoFaceConst.GUARANTEE_COMPANY_NAME;
import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_APP;
import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMddHHmmss;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
@Service
public class VideoFaceServiceImpl implements VideoFaceService {

    // log
    private static final Logger logger = LoggerFactory.getLogger(VideoFaceServiceImpl.class);

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    private BankRelaQuestionDOMapper bankRelaQuestionDOMapper;

    @Autowired
    private CarService carService;


    @Override
    @Transactional
    public ResultBean<Long> saveLog(VideoFaceLogDO videoFaceLogDO) {
        Preconditions.checkNotNull(videoFaceLogDO.getOrderId(), "订单号不能为空");

        // APP端  车型名称
        if (TYPE_APP.equals(videoFaceLogDO.getType())) {
            String carName = carService.getName(videoFaceLogDO.getCarDetailId(), CAR_DETAIL, CAR_MODEL);
            if (StringUtils.isNotBlank(carName)) {
                videoFaceLogDO.setCarName(carName);
            }
        }

        // 担保公司  就一个   写死
        videoFaceLogDO.setGuaranteeCompanyId(GUARANTEE_COMPANY_ID);
        videoFaceLogDO.setGuaranteeCompanyName(GUARANTEE_COMPANY_NAME);

        videoFaceLogDO.setGmtCreate(new Date());
        videoFaceLogDO.setGmtModify(new Date());
        int count = videoFaceLogDOMapper.insertSelective(videoFaceLogDO);
        Preconditions.checkArgument(count > 0, "保存失败");

        return ResultBean.ofSuccess(videoFaceLogDO.getId(), "保存成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLog(VideoFaceLogDO videoFaceLogDO) {
        Preconditions.checkArgument(null != videoFaceLogDO && null != videoFaceLogDO.getId(), "id不能为空");

        videoFaceLogDO.setGmtModify(new Date());
        int count = videoFaceLogDOMapper.updateByPrimaryKeySelective(videoFaceLogDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<List<VideoFaceLogVO>> listLog(VideoFaceQuery videoFaceQuery) {

        PageHelper.startPage(videoFaceQuery.getPageIndex(), videoFaceQuery.getPageSize(), true);

        List<VideoFaceLogDO> videoFaceLogDOList = videoFaceLogDOMapper.query(videoFaceQuery);

        List<VideoFaceLogVO> videoFaceLogVOList = videoFaceLogDOList.parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {

                    VideoFaceLogVO videoFaceLogVO = new VideoFaceLogVO();
                    BeanUtils.copyProperties(e, videoFaceLogVO);

                    return videoFaceLogVO;
                })
                .collect(Collectors.toList());

        // 取分页信息
        PageInfo<VideoFaceLogDO> pageInfo = new PageInfo<>(videoFaceLogDOList);

        return ResultBean.ofSuccess(videoFaceLogVOList, Math.toIntExact(pageInfo.getTotal()),
                pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<BankRelaQuestionDO>> listQuestion(Long bankId) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");

        List<BankRelaQuestionDO> bankRelaQuestionDOList = bankRelaQuestionDOMapper.listByBankIdAndType(bankId, null);

        return ResultBean.ofSuccess(bankRelaQuestionDOList);
    }

    @Override
    public ResultBean<String> exportLog(VideoFaceQuery videoFaceQuery) {

        // 需要导出的数据
        ResultBean<List<VideoFaceLogVO>> listResultBean = listLog(videoFaceQuery);
        Preconditions.checkArgument(listResultBean.getSuccess(), listResultBean.getMsg());
        List<VideoFaceLogVO> data = listResultBean.getData();

        // now
        String now = LocalDateTime.now().format(formatter_yyyyMMddHHmmss);

        // 文件名
        String exportFileName = "面签记录_" + now + ".xlsx";
        String[] cellTitle = {"担保公司名称", "客户姓名", "客户身份证号码", "视频路径", "定位位置", "面签类型",
                "面签员工", "生成时间", "审核结果"};

        // 声明一个工作薄
        XSSFWorkbook workBook = new XSSFWorkbook();

        // 生成一个表格
        XSSFSheet sheet = workBook.createSheet();
        workBook.setSheetName(0, "面签记录");

        // 创建表格标题行 第一行
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
            VideoFaceLogVO videoFaceLogVO = data.get(i);

            // 列
            row.createCell(0).setCellValue(videoFaceLogVO.getGuaranteeCompanyName());
            row.createCell(1).setCellValue(videoFaceLogVO.getCustomerName());
            row.createCell(2).setCellValue(videoFaceLogVO.getCustomerIdCard());
            row.createCell(3).setCellValue(videoFaceLogVO.getPath());
            row.createCell(4).setCellValue(videoFaceLogVO.getAddress());
            row.createCell(5).setCellValue(videoFaceLogVO.getTypeVal());
            row.createCell(6).setCellValue(videoFaceLogVO.getAuditorName());
            row.createCell(7).setCellValue(videoFaceLogVO.getGmtCreateStr());
            row.createCell(8).setCellValue(videoFaceLogVO.getActionVal());
        }

        // file
        File file = new File("/lib/" + exportFileName);

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
        OSSUnit.uploadObject2OSS(OSSUnit.getOSSClient(), file, OSSUnit.BUCKET_NAME, VideoFaceConst.OSS_DISK_NAME);

        return ResultBean.ofSuccess(VideoFaceConst.OSS_DISK_NAME + exportFileName);
    }

}
