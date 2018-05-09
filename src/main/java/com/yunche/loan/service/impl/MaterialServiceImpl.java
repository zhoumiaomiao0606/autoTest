package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.LoanCustomerEnum;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.CarUpdateParam;
import com.yunche.loan.domain.param.MaterialDownloadParam;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCreditInfoVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanFileService;
import com.yunche.loan.service.MaterialService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileEnum.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_CREDIT_RECORD;
import static com.yunche.loan.config.constant.LoanProcessEnum.SOCIAL_CREDIT_RECORD;

@Service
public class MaterialServiceImpl implements MaterialService {

    private final static Long CUSTOMER_DEFAULT_ID = (long) 0;

    private static final Set<String> URL_FILTER_SUFFIX = Sets.newHashSet("rar", "mp4", "mov", "avi", "m4v", "3gp");

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private MaterialAuditDOMapper materialAuditDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private OSSConfig ossConfig;
    @Autowired
    private MaterialDownHisDOMapper materialDownHisDOMapper;

    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper financialPlanDOMapper;

    @Resource
    private LoanFileDOMapper loanFileDOMapper;

    @Resource
    private LoanFileService loanFileService;


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
        recombinationVO.setLoanreview_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_loan_review", orderId));
        recombinationVO.setBusinessreview_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_business_review", orderId));
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
     * 资料审核客户批量文件下载（图片）
     * <p>
     * 打包文件，并上传至OSS，然后返回文件路径
     *
     * @param orderId
     * @param reGenerateZip 是否重新生成zip包
     */
    @Override
    public ResultBean<String> downloadFiles2OSS(Long orderId, Boolean reGenerateZip) {
        Preconditions.checkNotNull(orderId, "订单编号不能为空");

        Long customerId = null;

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "订单不存在");

        customerId = loanOrderDO.getLoanCustomerId();
        Preconditions.checkNotNull(customerId, "主贷人不存在");

        if (!reGenerateZip) {
            // 是否已经存在文件了
            List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, new Byte("26"), UPLOAD_TYPE_NORMAL);
            if (!CollectionUtils.isEmpty(loanFileDOS)) {
                LoanFileDO loanFileDO = loanFileDOS.get(0);
                if (null != loanFileDO) {
                    String path = loanFileDO.getPath();
                    if (StringUtils.isNotBlank(path)) {
                        return ResultBean.ofSuccess(path);
                    }
                }
            }
        }

        return packZipFile2OSS(orderId, customerId);
    }

    /**
     * 打包zip文件到OSS
     *
     * @param orderId
     * @param customerId
     * @return
     */
    private ResultBean<String> packZipFile2OSS(Long orderId, Long customerId) {
        String returnKey = null;
        FileInputStream fis = null;
        OSSClient ossClient = null;
        File zipFile = null;
        ZipOutputStream zos = null;

        try {

            List<MaterialDownloadParam> downloadParams = materialAuditDOMapper.selectDownloadMaterial(orderId, null);
            if (downloadParams == null) {
                return null;
            }
            downloadParams.stream().filter(Objects::nonNull)
                    .filter(e -> StringUtils.isNotBlank(e.getPath()))
                    .forEach(param -> {
                        String nameByCode = LoanFileEnum.getNameByCode(param.getType());
                        param.setTypeName(nameByCode);
                        List<String> list = JSONArray.parseArray(param.getPath(), String.class);
                        param.setPathList(list);
                    });

            // 初始化
            ossClient = OSSUnit.getOSSClient();
            String fileName = null;
            if (downloadParams != null) {
                fileName = downloadParams.get(0).getName() + "_" + downloadParams.get(0).getIdCard() + ".zip";
                customerId = downloadParams.get(0).getCustomerId();
            }
            // 创建临时文件
            zipFile = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
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
                for (String url : typeFile.getPathList()) {
                    OSSObject ossObject = OSSUnit.getObject(ossClient, url);
                    // 读去Object内容  返回
                    InputStream inputStream = ossObject.getObjectContent();
                    // 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
                    byte t = typeFile.getType();
                    String documentType = null;
                    switch (t) {
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                            documentType = "提车资料";
                            break;
                        case 12:
                        case 13:
                        case 16:
                        case 17:
                        case 18:
                            documentType = "上门家纺";
                            break;

                        default:
                            documentType = "基本资料";
                    }
                    zos.putNextEntry(new ZipEntry(documentType + "/" + typeFile.getTypeName() + "/" + url.split("/")[url.split("/").length - 1]));

                    int bytesRead = 0;
                    // 向压缩文件中输出数据
                    while ((bytesRead = inputStream.read()) != -1) {
                        zos.write(bytesRead);
                    }
                    inputStream.close();
                    zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
                }
            }

            fis = new FileInputStream(zipFile);
            String bucketName = ossConfig.getZipBucketName();
            if (StringUtil.isEmpty(bucketName)) {
                Preconditions.checkNotNull("OSS压缩文件上传目录不存在");
            }
            String diskName = ossConfig.getZipDiskName();
            OSSUnit.uploadObject2OSS(ossClient, zipFile, bucketName, diskName + File.separator);

//            // 设置URL过期时间为1小时
//            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
//            URL url = ossClient.generatePresignedUrl(bucketName, zipFile.getName(), expiration);
            returnKey = diskName + File.separator + zipFile.getName();

        } catch (Exception e) {
            throw new RuntimeException("文件打包/上传/下载失败", e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (ossClient != null) {
                    ossClient.shutdown();
                }
                if (zipFile != null) {
                    // 删除临时文件
                    zipFile.delete();
                }
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("释放资源失败", e);
            }
        }

        // zip包路径，存储到loan_file表
        saveToLoanFile(customerId, returnKey);

        return ResultBean.ofSuccess(returnKey, "下载完成");
    }

    /**
     * 客户资料下载
     *
     * @param request
     * @param response
     * @param orderId
     * @return
     */
    @Override
    public String zipFilesDown(HttpServletRequest request, HttpServletResponse response, Long orderId, String taskDefinitionKey, Long customerId) {
        OSSClient ossClient = null;
        ZipOutputStream zos = null;
        BufferedInputStream buff = null;
        FileInputStream fis = null;
        BufferedOutputStream out = null;
        File zipFile = null;
        try {

            List<MaterialDownloadParam> downloadParams = materialAuditDOMapper.selectDownloadMaterial(orderId, customerId);
            if (downloadParams == null) {
                return null;
            }
            downloadParams.parallelStream().filter(Objects::nonNull)
                    .filter(e -> StringUtils.isNotBlank(e.getPath()))
                    .forEach(param -> {
                        String nameByCode = LoanFileEnum.getNameByCode(param.getType());
                        param.setTypeName(nameByCode);
                        String custTypeName = LoanCustomerEnum.getNameByCode(param.getCustType());
                        param.setCustTypeName(custTypeName);
                        List<String> list = JSONArray.parseArray(param.getPath(), String.class);

                        List<String> unique = list.stream().distinct().collect(Collectors.toList());
                        param.setPathList(unique);
                    });

            // 初始化
            ossClient = OSSUnit.getOSSClient();
            String fileName = null;
            if (downloadParams != null) {
                fileName = downloadParams.get(0).getName() + "_" + downloadParams.get(0).getIdCard() + ".zip";
            }
            // 创建临时文件
            zipFile = File.createTempFile(fileName, ".zip");
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
                for (String url : typeFile.getPathList()) {
                    OSSObject ossObject = OSSUnit.getObject(ossClient, url);
                    // 读去Object内容  返回
                    InputStream inputStream = ossObject.getObjectContent();
                    // 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
                    byte t = typeFile.getType();
                    String documentType = null;

                    switch (t) {
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                            documentType = "提车资料";
                            break;
                        case 12:
                        case 13:
                        case 16:
                        case 17:
                        case 18:
                            documentType = "上门家纺";
                            break;

                        default:
                            documentType = "基本资料";

                    }

                    String[] urlArr = url.split("\\.");
                    if (ArrayUtils.isNotEmpty(urlArr) && urlArr.length == 2) {
                        String urlSuffix = urlArr[1].trim().toLowerCase();

                        if (HOME_VISIT_VIDEO.getType() == t || URL_FILTER_SUFFIX.contains(urlSuffix)) {
                            continue;
                        }
                    }

                    if (taskDefinitionKey != null && (taskDefinitionKey.equals(BANK_CREDIT_RECORD.getCode()) || taskDefinitionKey.equals(SOCIAL_CREDIT_RECORD.getCode()))) {
//                        if(typeFile.getCustType().equals(PRINCIPAL_LENDER.getType())){
                        if (t == ID_CARD_FRONT.getType() || t == ID_CARD_BACK.getType() || t == AUTH_BOOK.getType() || t == AUTH_BOOK_SIGN_PHOTO.getType()) {
                            if (customerId != null) {
                                zos.putNextEntry(new ZipEntry(url.split("/")[url.split("/").length - 1]));
                            } else {
                                zos.putNextEntry(new ZipEntry(typeFile.getCustTypeName() + "/" + url.split("/")[url.split("/").length - 1]));
                            }

                        } else {
                            continue;
                        }
//                        }else{
//                            continue;
//                        }
                    } else {
                        zos.putNextEntry(new ZipEntry(typeFile.getCustTypeName() + "/" + documentType + "/" + typeFile.getTypeName() + "/" + url.split("/")[url.split("/").length - 1]));

                    }

                    int bytesRead = 0;
                    // 向压缩文件中输出数据
                    while ((bytesRead = inputStream.read()) != -1) {
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
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

            fis = new FileInputStream(zipFile);
            buff = new BufferedInputStream(fis);
            out = new BufferedOutputStream(response.getOutputStream());
            byte[] tmpCache = new byte[1024];
            int l = 0;
            while (l < zipFile.length()) {
                int j = buff.read(tmpCache, 0, 1024);
                l += j;
                out.write(tmpCache, 0, j);
            }
        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        } finally {
            try {
                if (buff != null) {
                    buff.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (out != null) {
                    out.close();
                }
                if (ossClient != null) {
                    ossClient.shutdown();
                }

                if (zipFile != null) {
                    // 删除临时文件
//                    zipFile.delete();
                }
                if (zos != null) {
                    zos.close();
                }


            } catch (IOException e) {
                Preconditions.checkArgument(false, e.getMessage());

            }
        }

        return null;
    }


    /**
     * @param orderId
     * @param taskDefinitionKey
     * @param customerId
     * @return
     */
    @Override
    public ResultBean down2tomcat(Long orderId, String taskDefinitionKey, Long customerId) {
        OSSClient ossClient = null;
        ZipOutputStream zos = null;
        BufferedInputStream buff = null;
        FileInputStream fis = null;
        BufferedOutputStream out = null;
        File zipFile = null;
        String fileName = null;
        try {
            MaterialDownHisDOKey downHisDOKey = new MaterialDownHisDOKey();
            downHisDOKey.setOrderId(orderId);
            downHisDOKey.setTaskDefinitionKey(taskDefinitionKey);
            if (customerId == null) {
                downHisDOKey.setCustomerId(CUSTOMER_DEFAULT_ID);
            } else {
                downHisDOKey.setCustomerId(customerId);
            }

            MaterialDownHisDO downHisDO = materialDownHisDOMapper.selectByPrimaryKey(downHisDOKey);
            if (downHisDO != null) {
                return ResultBean.ofSuccess(downHisDO.getUrl(), "下载完成");
            }
            List<MaterialDownloadParam> downloadParams = materialAuditDOMapper.selectDownloadMaterial(orderId, customerId);
            if (downloadParams == null) {
                return null;
            }
            downloadParams.parallelStream().filter(Objects::nonNull)
                    .filter(e -> StringUtils.isNotBlank(e.getPath()))
                    .forEach(param -> {
                        String nameByCode = LoanFileEnum.getNameByCode(param.getType());
                        param.setTypeName(nameByCode);
                        String custTypeName = LoanCustomerEnum.getNameByCode(param.getCustType());
                        param.setCustTypeName(custTypeName);
                        List<String> list = JSONArray.parseArray(param.getPath(), String.class);
                        param.setPathList(list);
                    });

            // 初始化
            ossClient = OSSUnit.getOSSClient();

            if (downloadParams != null) {
                fileName = downloadParams.get(0).getName() + "_" + downloadParams.get(0).getIdCard() + ".zip";
            }
//            // 创建临时文件
//            zipFile = File.createTempFile(fileName, ".zip");
            // 创建临时文件
            File dir = new File(ossConfig.getDown2tomcatBasepath() + File.separator + taskDefinitionKey);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            zipFile = new File(ossConfig.getDown2tomcatBasepath() + File.separator + taskDefinitionKey + File.separator + fileName);
            zipFile.createNewFile();
            FileOutputStream f = new FileOutputStream(zipFile);

            /**
             *
             * 作用是为任何OutputStream产生校验和
             * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
             */
            CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
            // 用于将数据压缩成Zip文件格式
            zos = new ZipOutputStream(csum);

            for (MaterialDownloadParam typeFile : downloadParams) {
                // 获取Object，返回结果为OSSObject对象
                for (String url : typeFile.getPathList()) {
                    OSSObject ossObject = OSSUnit.getObject(ossClient, url);
                    // 读去Object内容  返回
                    InputStream inputStream = ossObject.getObjectContent();
                    // 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
                    byte t = typeFile.getType();
                    String documentType = null;

                    switch (t) {
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                            documentType = "提车资料";
                            break;
                        case 12:
                        case 13:
                        case 16:
                        case 17:
                        case 18:
                            documentType = "上门家纺";
                            break;

                        default:
                            documentType = "基本资料";

                    }

                    if (url.endsWith(".rar")) {
                        continue;
                    }
                    if (taskDefinitionKey != null && (taskDefinitionKey.equals(BANK_CREDIT_RECORD.getCode()) || taskDefinitionKey.equals(SOCIAL_CREDIT_RECORD.getCode()))) {
//                        if(typeFile.getCustType().equals(PRINCIPAL_LENDER.getType())){
                        if (t == ID_CARD_FRONT.getType() || t == ID_CARD_BACK.getType() || t == AUTH_BOOK.getType() || t == AUTH_BOOK_SIGN_PHOTO.getType()) {
                            if (customerId != null) {
                                zos.putNextEntry(new ZipEntry(url.split("/")[url.split("/").length - 1]));
                            } else {
                                zos.putNextEntry(new ZipEntry(typeFile.getCustTypeName() + "/" + url.split("/")[url.split("/").length - 1]));
                            }

                        } else {
                            continue;
                        }
//                        }else{
//                            continue;
//                        }
                    } else {
                        zos.putNextEntry(new ZipEntry(typeFile.getCustTypeName() + "/" + documentType + "/" + typeFile.getTypeName() + "/" + url.split("/")[url.split("/").length - 1]));

                    }

                    int bytesRead = 0;
                    // 向压缩文件中输出数据
                    while ((bytesRead = inputStream.read()) != -1) {
                        zos.write(bytesRead);
                    }
                    inputStream.close();
                    zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
                }
            }
            zos.close();
            MaterialDownHisDO materialDownHisDO = new MaterialDownHisDO();
            materialDownHisDO.setOrderId(orderId);
            if (taskDefinitionKey != null) {
                materialDownHisDO.setTaskDefinitionKey(taskDefinitionKey);
            }
            if (customerId != null) {
                materialDownHisDO.setCustomerId(customerId);
            } else {
                materialDownHisDO.setCustomerId(CUSTOMER_DEFAULT_ID);
            }
            materialDownHisDO.setGmtCreate(new Date());

            materialDownHisDO.setStatus(VALID_STATUS);
            materialDownHisDO.setUrl(ossConfig.getDown2tomcatBasepath() + File.separator + fileName);
            materialDownHisDOMapper.insertSelective(materialDownHisDO);

        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        } finally {
            try {
                if (buff != null) {
                    buff.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (out != null) {
                    out.close();
                }
                if (ossClient != null) {
                    ossClient.shutdown();
                }

//                if(zipFile!=null){
//                    // 删除临时文件
////                    zipFile.delete();
//                }
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                Preconditions.checkArgument(false, e.getMessage());

            }
        }
        return ResultBean.ofSuccess(ossConfig.getDown2tomcatBasepath() + File.separator + taskDefinitionKey + File.separator + fileName, "下载完成");
    }

    @Override
    public void carUpdate(CarUpdateParam param) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()), new Byte("0"));
        Long vid = loanOrderDO.getVehicleInformationId();
        Long cid = loanOrderDO.getLoanCarInfoId();
        Long fid = loanOrderDO.getLoanFinancialPlanId();

        if (vid != null && checkParamsNotNull(
                param.getVehicle_vehicle_identification_number(), param.getVehicle_license_plate_number(),
                param.getVehicle_engine_number(), param.getVehicle_apply_license_plate_area(),
                param.getVehicle_registration_certificate_number(), param.getVehicle_color(),
                param.getVehicle_customize_brand(), param.getVehicle_purchase_car_invoice_price(),
                param.getVehicle_invoice_down_payment(), param.getVehicle_purchase_car_invoice_date(),
                param.getVehicle_invoice_car_dealer(), param.getVehicle_displacement(),
                param.getVehicle_register_date(), param.getQualified_certificate_number()
        )) {
            VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
            vehicleInformationDO.setId(vid);
            vehicleInformationDO.setVehicle_identification_number(param.getVehicle_vehicle_identification_number());
            vehicleInformationDO.setLicense_plate_number(param.getVehicle_license_plate_number());
            vehicleInformationDO.setEngine_number(param.getVehicle_engine_number());
            vehicleInformationDO.setApply_license_plate_area(param.getVehicle_apply_license_plate_area());
            vehicleInformationDO.setRegistration_certificate_number(param.getVehicle_registration_certificate_number());
            vehicleInformationDO.setColor(param.getVehicle_color());
            vehicleInformationDO.setCustomize_brand(param.getVehicle_customize_brand());
            vehicleInformationDO.setPurchase_car_invoice_price(StringUtils.isBlank(param.getVehicle_purchase_car_invoice_price()) ? null : new BigDecimal(param.getVehicle_purchase_car_invoice_price()));
            vehicleInformationDO.setInvoice_down_payment(StringUtils.isBlank(param.getVehicle_invoice_down_payment()) ? null : new BigDecimal(param.getVehicle_invoice_down_payment()));
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");

            try {
                vehicleInformationDO.setPurchase_car_invoice_date(StringUtils.isBlank(param.getVehicle_purchase_car_invoice_date()) ? null : myFmt.parse(param.getVehicle_purchase_car_invoice_date()));
            } catch (ParseException e) {
                throw new BizException("日期转化失败");
            }
            vehicleInformationDO.setInvoice_car_dealer(param.getVehicle_invoice_car_dealer());
            vehicleInformationDO.setDisplacement(param.getVehicle_displacement());
            try {
                vehicleInformationDO.setRegister_date(StringUtils.isBlank(param.getVehicle_register_date()) ? null : myFmt.parse(param.getVehicle_register_date()));
            } catch (ParseException e) {
                throw new BizException("日期转化失败");
            }
            vehicleInformationDO.setQualified_certificate_number(param.getQualified_certificate_number());

            vehicleInformationDOMapper.updateByPrimaryKeySelective(vehicleInformationDO);
        }

        if (cid != null && checkParamsNotNull(param.getCar_vehicle_property(), param.getCar_type())) {
            LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
            loanCarInfoDO.setId(cid);
            if (!StringUtils.isBlank(param.getCar_vehicle_property())) {
                loanCarInfoDO.setVehicleProperty(new Byte(param.getCar_vehicle_property()));
            }
            if (!StringUtils.isBlank(param.getCar_type())) {
                loanCarInfoDO.setCarType(new Byte(param.getCar_type()));
            }
            loanCarInfoDOMapper.updateByPrimaryKeySelective(loanCarInfoDO);
        }

        if (fid != null && checkParamsNotNull(param.getCar_price(), param.getCar_price())) {
            LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
            loanFinancialPlanDO.setId(fid);
            if (!StringUtils.isBlank(param.getCar_price())) {
                loanFinancialPlanDO.setCarPrice(new BigDecimal(param.getCar_price()));
            }
            if (!StringUtils.isBlank(param.getCar_price())) {
                loanFinancialPlanDO.setAppraisal(new BigDecimal(param.getFinancial_appraisal()));
            }
            financialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
        }
    }

    private boolean checkParamsNotNull(String... args) {
        for (String str : args) {
            if (StringUtils.isNotBlank(str)) {
                return true;
            }
        }

        return false;
    }

    /**
     * zip包路径，存储到loan_file表
     *
     * @param customerId
     * @param path
     */
    private void saveToLoanFile(Long customerId, String path) {
        if (null != customerId && StringUtils.isNotBlank(path)) {
            LoanFileDO loanFileDO = new LoanFileDO();
            loanFileDO.setCustomerId(customerId);
            loanFileDO.setUploadType(UPLOAD_TYPE_NORMAL);
            loanFileDO.setPath(path);
            loanFileDO.setType(new Byte("26"));
            loanFileService.create(loanFileDO);
        }
    }
}
