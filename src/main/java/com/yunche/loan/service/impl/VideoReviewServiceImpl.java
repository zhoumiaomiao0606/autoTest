package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.query.VideoFaceExportQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.ZhonganInfoDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VideoReviewService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.yunche.loan.config.constant.LoanFileEnum.*;

/**
 * @author liuzhe
 * @date 2018/9/12
 */
@Service
public class VideoReviewServiceImpl implements VideoReviewService {


    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private ZhonganInfoDOMapper zhonganInfoDOMapper;

    @Autowired
    private OSSConfig ossConfig;


    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();

        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalBaseInfo(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));

        // 视频面签视频
        recombinationVO.setPath(loanQueryService.selectVideoFacePath(orderId));

        // 55-签字视频; 56-问话视频;
        Set<Byte> fileTypes = Sets.newHashSet(SIGNATURE_VIDEO.getType(), INTERROGATION_VIDEO.getType());
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, fileTypes));

        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    public String videoFaceExport(VideoFaceExportQuery query) {
        List<VideoFaceExportVO> list = zhonganInfoDOMapper.videoFaceExpot(query);

        return videoFaceExcelFile(list);
    }
    private String videoFaceExcelFile(List<VideoFaceExportVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "视频审核"+timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {

            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("单号", "领取时间", "合伙人", "客户姓名",
                    "视频问题", "是否通过", "通过时间"
            );
            XSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            XSSFRow row = null;
            XSSFCell cell = null;
            for (int i = 0; i < list.size(); i++) {
                VideoFaceExportVO videoFaceExportVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(videoFaceExportVO.getTaskId());

                cell = row.createCell(1);
                cell.setCellValue(videoFaceExportVO.getGetTime());

                cell = row.createCell(2);
                cell.setCellValue(videoFaceExportVO.getPName());

                cell = row.createCell(3);
                cell.setCellValue(videoFaceExportVO.getCName());

                cell = row.createCell(4);
                cell.setCellValue(videoFaceExportVO.getInfo());

                cell = row.createCell(5);
                cell.setCellValue(videoFaceExportVO.getResult());

                cell = row.createCell(6);
                cell.setCellValue(videoFaceExportVO.getFinishTime());
            }
            //文件宽度自适应
            sheet.autoSizeColumn((short) 0);
            sheet.autoSizeColumn((short) 1);
            sheet.autoSizeColumn((short) 2);
            sheet.autoSizeColumn((short) 3);
            sheet.autoSizeColumn((short) 4);
            sheet.autoSizeColumn((short) 5);
            sheet.autoSizeColumn((short) 6);

            workbook.write(out);
            //上传OSS
            OSSClient ossClient = OSSUnit.getOSSClient();
            String bucketName = ossConfig.getBucketName();
            String diskName = ossConfig.getDownLoadDiskName();
            OSSUnit.deleteFile(ossClient, bucketName, diskName + File.separator, fileName);
            OSSUnit.uploadObject2OSS(ossClient, file, bucketName, diskName + File.separator);
        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Preconditions.checkArgument(false, e.getMessage());
            }
        }

        return ossConfig.getDownLoadDiskName() + File.separator + fileName;
    }
}
