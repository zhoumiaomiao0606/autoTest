package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.domain.entity.MaterialAuditDO;
import com.yunche.loan.domain.param.MaterialDownloadParam;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.MaterialAuditDOMapper;
import com.yunche.loan.service.LoanProcessLogService;
import com.yunche.loan.service.MaterialService;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private MaterialAuditDOMapper materialAuditDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanProcessLogService loanProcessLogService;

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public RecombinationVO detail(Long orderId) {
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for (UniversalCreditInfoVO universalCreditInfoVO : credits) {
            if (!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())) {
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId, Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setRelations(loanQueryDOMapper.selectUniversalRelationCustomer(orderId));
        recombinationVO.setLoan(loanQueryDOMapper.selectUniversalLoanInfo(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setCredits(credits);
        recombinationVO.setTelephone_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_telephone_verify", orderId));
        recombinationVO.setSupplement(loanQueryDOMapper.selectUniversalSupplementInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalMaterialRecord(orderId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    @Transactional
    public void update(MaterialUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()), new Byte("0"));
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }

        Long foundationId = loanOrderDO.getMaterialAuditId();//关联ID
        if (foundationId == null) {
            //新增提交
            MaterialAuditDO V = BeanPlasticityUtills.copy(MaterialAuditDO.class, param);
            materialAuditDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setMaterialAuditId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {
            if (materialAuditDOMapper.selectByPrimaryKey(foundationId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                MaterialAuditDO V = BeanPlasticityUtills.copy(MaterialAuditDO.class, param);
                V.setId(foundationId);
                materialAuditDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                MaterialAuditDO V = BeanPlasticityUtills.copy(MaterialAuditDO.class, param);
                V.setId(foundationId);
                materialAuditDOMapper.updateByPrimaryKeySelective(V);
            }
        }
    }

    /**
     * 资料审核客户批量文件下载上传至OSS（图片）
     *
     * @param orderId
     */
    @Override
    public ResultBean downloadFilesToOSS(Long orderId) {
        Preconditions.checkNotNull(orderId,"订单编号不能为空");
        String returnKey =null;
        FileInputStream fis=null;
        OSSClient ossClient=null;
        File zipFile=null;
        ZipOutputStream zos=null;
        try {

            List<MaterialDownloadParam> downloadParams = materialAuditDOMapper.selectDownloadMaterial(orderId);
            if(downloadParams==null){
                return null;
            }
            downloadParams.parallelStream().filter(Objects::nonNull)
                    .filter(e -> StringUtils.isNotBlank(e.getPath()))
                    .forEach(param ->{
                        String nameByCode = LoanFileEnum.getNameByCode(param.getType());
                        param.setTypeName(nameByCode);
                        List<String> list = JSONArray.parseArray(param.getPath(), String.class);
                        param.setPathList(list);
                    });

            // 初始化
            ossClient =  OSSUnit.getOSSClient();
            String fileName=null;
            if(downloadParams!=null){
                fileName = downloadParams.get(0).getName()+"_"+downloadParams.get(0).getIdCard()+".zip";
            }
            // 创建临时文件
            zipFile = new File(ossConfig.getDownLoadBasepath()+File.separator+fileName);
            zipFile.createNewFile();

            FileOutputStream f = new FileOutputStream(zipFile);
            /**
             * 作用是为任何OutputStream产生校验和
             * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
             */
            CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
            // 用于将数据压缩成Zip文件格式
            zos = new ZipOutputStream(csum);

            for (MaterialDownloadParam typeFile : downloadParams) {
                // 获取Object，返回结果为OSSObject对象
                for(String url : typeFile.getPathList()){
                    OSSObject ossObject = OSSUnit.getObject(ossClient,url);
                    // 读去Object内容  返回
                    InputStream inputStream = ossObject.getObjectContent();
                    // 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
                    byte t=  typeFile.getType();
                    String documentType=null;
                    switch (t){
                        case 19:
                        case 20:
                        case 21:
                        case 22:documentType="提车资料";break;
                        case 12:
                        case 13:
                        case 16:
                        case 17:
                        case 18:documentType="上门家纺";break;

                        default:documentType="基本资料";

                    }
                    zos.putNextEntry(new ZipEntry(documentType+"/"+typeFile.getTypeName()+"/"+url.split("/")[url.split("/").length-1]));

                    int bytesRead = 0;
                    // 向压缩文件中输出数据
                    while((bytesRead=inputStream.read())!=-1){
                        zos.write(bytesRead);
                    }
                    inputStream.close();
                    zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
                }
            }

            fis = new FileInputStream(zipFile);
            String bucketName = ossConfig.getZipBucketName();
            if(StringUtil.isEmpty(bucketName)){
                Preconditions.checkNotNull("OSS压缩文件上传目录不存在");
            }
            String diskName = ossConfig.getZipDiskName();
            OSSUnit.uploadObject2OSS(ossClient, zipFile, bucketName, diskName+File.separator);

//            // 设置URL过期时间为1小时
//            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
//            URL url = ossClient.generatePresignedUrl(bucketName, zipFile.getName(), expiration);
            returnKey =diskName+File.separator+zipFile.getName();

        } catch (Exception e) {
            Preconditions.checkArgument(false,e.getMessage());
        }finally {
            try {
                if(fis!=null){
                    fis.close();
                }
                if(ossClient!=null){
                    ossClient.shutdown();
                }
               if(zipFile!=null){
                   // 删除临时文件
                   zipFile.delete();

               }
               if(zos!=null){
                   zos.close();
               }
            } catch (IOException e) {
               Preconditions.checkArgument(false,e.getMessage());
            }
        }
        return ResultBean.ofSuccess(returnKey,"下载完成");
    }

    /**
     * 客户资料下载
     * @param request
     * @param response
     * @param orderId
     * @return
     */
    public  ResultBean<String> zipFilesDown(HttpServletRequest request, HttpServletResponse response,Long orderId){

        try {

            List<MaterialDownloadParam> downloadParams = materialAuditDOMapper.selectDownloadMaterial(orderId);
            if(downloadParams==null){
                return null;
            }
            downloadParams.parallelStream().filter(Objects::nonNull)
                    .filter(e -> StringUtils.isNotBlank(e.getPath()))
                    .forEach(param ->{
                        String nameByCode = LoanFileEnum.getNameByCode(param.getType());
                        param.setTypeName(nameByCode);
                        List<String> list = JSONArray.parseArray(param.getPath(), String.class);
                        param.setPathList(list);
                    });

            // 初始化
            OSSClient ossClient =  OSSUnit.getOSSClient();
            String fileName=null;
            if(downloadParams!=null){
                 fileName = downloadParams.get(0).getName()+"_"+downloadParams.get(0).getIdCard()+".zip";
            }
            // 创建临时文件
            File zipFile = File.createTempFile(fileName, ".zip");
            FileOutputStream f = new FileOutputStream(zipFile);
            /**
             * 作用是为任何OutputStream产生校验和
             * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
             */
            CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
            // 用于将数据压缩成Zip文件格式
            ZipOutputStream zos = new ZipOutputStream(csum);

            for (MaterialDownloadParam typeFile : downloadParams) {
                // 获取Object，返回结果为OSSObject对象
                for(String url : typeFile.getPathList()){
                    OSSObject ossObject = OSSUnit.getObject(ossClient,url);
                    // 读去Object内容  返回
                    InputStream inputStream = ossObject.getObjectContent();
                    // 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
                    byte t=  typeFile.getType();
                    String documentType=null;
                    switch (t){
                        case 19:
                        case 20:
                        case 21:
                        case 22:documentType="提车资料";break;
                        case 12:
                        case 13:
                        case 16:
                        case 17:
                        case 18:documentType="上门家纺";break;

                        default:documentType="基本资料";

                    }
                    zos.putNextEntry(new ZipEntry(documentType+"/"+typeFile.getTypeName()+"/"+url.split("/")[url.split("/").length-1]));

                    int bytesRead = 0;
                    // 向压缩文件中输出数据
                    while((bytesRead=inputStream.read())!=-1){
                        zos.write(bytesRead);
                    }
                    inputStream.close();
                    zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
                }
            }
            zos.close();

            fileName = new String(fileName.getBytes(), "utf-8");

            response.reset();
            response.setContentType("text/plain");
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Location", fileName);
            response.setHeader("Cache-Control", "max-age=0");
            response.setHeader("Content-Disposition", "attachment; filename=" +java.net.URLEncoder.encode(fileName, "UTF-8"));

            FileInputStream fis = new FileInputStream(zipFile);
            BufferedInputStream buff = new BufferedInputStream(fis);
            BufferedOutputStream out=new BufferedOutputStream(response.getOutputStream());
            byte[] car=new byte[1024];
            int l=0;
            while (l < zipFile.length()) {
                int j = buff.read(car, 0, 1024);
                l += j;
                out.write(car, 0, j);
            }
            // 关闭流
            fis.close();
            buff.close();
            out.close();

            ossClient.shutdown();
            // 删除临时文件
            zipFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.ofSuccess("下载完成");
    }

}
