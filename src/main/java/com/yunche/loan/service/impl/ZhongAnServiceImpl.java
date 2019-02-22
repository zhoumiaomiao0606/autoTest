package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.constant.ZhongAnConfig;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ZhongAnCreditStructParam;
import com.yunche.loan.service.ZhongAnService;
import com.zhongan.scorpoin.biz.common.CommonRequest;
import com.zhongan.scorpoin.biz.common.CommonResponse;
import com.zhongan.scorpoin.common.ZhongAnOpenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liuzhe
 * @date 2019/2/21
 */
@Slf4j
@Service
public class ZhongAnServiceImpl implements ZhongAnService {


    @Override
    public Object creditStruct(ZhongAnCreditStructParam param) throws ZhongAnOpenException {

        String serviceName = "zhongan.xdecision.apply";
        JSONObject jsonData = setAndGetJsonData(param);

        // 业务参数
        JSONObject params = new JSONObject();
        params.put("merchKey", "D63EB54EDE6021213B8D06B11D348C5A");
        params.put("applyNo", "" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000000000));
        params.put("productCode", "YCJRCD");
        params.put("ruleCode", "YCJRCD757006");
        params.put("jsonData", jsonData.toJSONString());

        CommonRequest request = new CommonRequest(serviceName);
        request.setParams(params);

        log.info("众安接口：request  >>>  {}", JSON.toJSONString(request));
        long startTime = System.currentTimeMillis();

        CommonResponse response = (CommonResponse) ZhongAnConfig.getClient().call(request);

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("众安接口：totalTime : {}s  ,   response  >>>  {}",
                new Double(totalTime) / 1000, JSON.toJSONString(response));

        return null;
    }

    /**
     * JsonData组装
     *
     * @param param
     * @return
     */
    private JSONObject setAndGetJsonData(ZhongAnCreditStructParam param) {

        CreditStructBlackAshSignDO creditStructBlackAshSign = param.getCreditStructBlackAshSign();
        CreditStructQueryCountDO creditStructQueryCount = param.getCreditStructQueryCount();
//        CreditStructSumDO creditStructSum = param.getCreditStructSum();

        List<CreditStructTradeDetailDO> creditStructTradeDetail = param.getCreditStructTradeDetail();
        List<CreditStructTradeDetailLoanDO> creditStructTradeDetailLoan = param.getCreditStructTradeDetailLoan();
        List<CreditStructGuaranteeLoanDetailDO> creditStructGuaranteeLoanDetail = param.getCreditStructGuaranteeLoanDetail();
//        List<CreditStructGuaranteeCreditCardDetailDO> creditStructGuaranteeCreditCardDetail = param.getCreditStructGuaranteeCreditCardDetail();


        JSONObject jsonData = new JSONObject();

        // 客户角色：主/共/担
        Byte customerType = param.getCustomerType();
        String identity = "";
        if (null != customerType) {
            switch (customerType) {
                case 1:
                    identity = "主贷人";
                    break;
                case 2:
                    identity = "共贷人";
                    break;
                case 3:
                    identity = "担保人";
                    break;
                case 4:
                    identity = "紧急联系人";
                    break;
            }
        }
        jsonData.put("identity", identity);


        // ------- 黑灰标志     creditStructBlackAshSign
        if (null != creditStructBlackAshSign) {

            Byte hasForbidSign = creditStructBlackAshSign.getHasForbidSign();
            Byte hasFollowSign = creditStructBlackAshSign.getHasFollowSign();
            Byte hasRefuseSign = creditStructBlackAshSign.getHasRefuseSign();
            Byte hasBlackListSign = creditStructBlackAshSign.getHasBlackListSign();

            Byte if_blk = 0;
            if (BaseConst.K_YORN_YES.equals(hasForbidSign) || BaseConst.K_YORN_YES.equals(hasFollowSign)
                    || BaseConst.K_YORN_YES.equals(hasRefuseSign) || BaseConst.K_YORN_YES.equals(hasBlackListSign)) {
                if_blk = 1;
            }

            // 是否有禁入/关注/拒贷/黑名单标志
            jsonData.put("if_blk", if_blk);
            // 是否为流程灰名单
            jsonData.put("if_prc_gry", creditStructBlackAshSign.getIsProcessAshList());
            // 是否曾有灰名单
            jsonData.put("if_his_gry", creditStructBlackAshSign.getIsOnceAshList());
            // 是否为灰名单
            jsonData.put("if_gry", creditStructBlackAshSign.getIsAshList());
        }


        // ------- 查询类信息        creditStructQueryCount
        if (null != creditStructQueryCount) {
            // 最近2个月贷款查询次数
            jsonData.put("loa_lst_2mt_qry_cnt", creditStructQueryCount.getLoanQueryCountLastMonth2());
            // 最近2个月信用卡查询次数
            jsonData.put("crd_lst_2mt_qry_cnt", creditStructQueryCount.getCreditQueryCountCardLastMonth2());
        }


        // -------- 信贷交易信息明细——贷款        creditStructTradeDetailLoan
        final int[] loa_lst_12mth_ovd_sum = {0};
        final int[] loa_lst_24mth_ovd_max = {0};

        final BigDecimal[] mth_rep_sum = {BigDecimal.valueOf(0)};
        final BigDecimal[] non_mth_amt_sum = {BigDecimal.valueOf(0)};

        final Integer[] gz_loa_cnt = {0};
        final Integer[] cj_loa_cnt = {0};
        final Integer[] ky_loa_cnt = {0};
        final Integer[] ss_loa_cnt = {0};
        final Integer[] dz_loa_cnt = {0};

        List<Object> lst_1mth_loa_list = Lists.newArrayList();
        List<Object> aft_1mth_loa_list = Lists.newArrayList();
        List<Object> ovd_loa_dtl_list = Lists.newArrayList();

        creditStructTradeDetailLoan.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 12月内累计逾期次数
                    Integer overdueTotalNumLastMonth12 = e.getOverdueTotalNumLastMonth12();
                    if (null != overdueTotalNumLastMonth12) {
                        loa_lst_12mth_ovd_sum[0] += overdueTotalNumLastMonth12;
                    }

                    // 24月内最长逾期期数
                    Integer overdueMaxNumLastMonth24 = e.getOverdueMaxNumLastMonth24();
                    if (null != overdueMaxNumLastMonth24) {
                        loa_lst_24mth_ovd_max[0] = overdueMaxNumLastMonth24 > loa_lst_24mth_ovd_max[0] ?
                                overdueMaxNumLastMonth24 : loa_lst_24mth_ovd_max[0];
                    }

                    // 本月应还款
                    BigDecimal currentMonthReapy = e.getCurrentMonthReapy();
                    if (null != currentMonthReapy) {
                        mth_rep_sum[0] = mth_rep_sum[0].add(currentMonthReapy);
                    }

                    // 还款方式：1-按月归还;2-一次性归还;3-按季归还;4-不定期归还;
                    Byte repayWay = e.getRepayWay();
                    if (null != repayWay) {

                        if (repayWay != 1) {
                            // 本金余额
                            BigDecimal principalBalance = e.getPrincipalBalance();
                            if (null != principalBalance) {
                                non_mth_amt_sum[0] = non_mth_amt_sum[0].add(principalBalance);
                            }
                        }
                    }


                    // 五级分类：1-正常;2-关注;3-次级;4-可疑;5-损失;6-呆账;
                    Byte accountType = e.getAccountType();
                    if (null != accountType) {
                        if (accountType == 1) {

                        } else if (accountType == 2) {
                            gz_loa_cnt[0] += 1;
                        } else if (accountType == 3) {
                            cj_loa_cnt[0] += 1;
                        } else if (accountType == 4) {
                            ky_loa_cnt[0] += 1;
                        } else if (accountType == 5) {
                            ss_loa_cnt[0] += 1;
                        } else if (accountType == 6) {
                            dz_loa_cnt[0] += 1;
                        }
                    }

                    Map<String, Object> lst_1mth_loa = Maps.newHashMap();
                    lst_1mth_loa.put("到期日期", e.getExpireDate());
                    lst_1mth_loa.put("本金余额", e.getPrincipalBalance());
                    lst_1mth_loa.put("贷款类型", e.getType());
                    lst_1mth_loa.put("账户状态", e.getAccountStatus());
                    lst_1mth_loa_list.add(lst_1mth_loa);

                    Map<String, Object> aft_1mth_loa = Maps.newHashMap();
                    aft_1mth_loa.put("到期日期", e.getExpireDate());
                    aft_1mth_loa.put("本金余额", e.getPrincipalBalance());
                    aft_1mth_loa.put("贷款类型", e.getType());
                    aft_1mth_loa.put("账户状态", e.getAccountStatus());
                    aft_1mth_loa_list.add(aft_1mth_loa);

                    Map<String, Object> ovd_loa_dtl = Maps.newHashMap();
                    ovd_loa_dtl.put("账户状态", e.getAccountStatus());
                    ovd_loa_dtl.put("当前逾期金额", e.getCurrentOverdueMoney());
                    ovd_loa_dtl_list.add(ovd_loa_dtl);
                });


        // 贷款近12个月累计逾期次数        -> sum(12月内累计逾期次数)
        jsonData.put("loa_lst_12mth_ovd_sum", loa_lst_12mth_ovd_sum[0]);
        // 贷款近24个月最长逾期期数        -> max(24月内最长逾期期数)
        jsonData.put("loa_lst_24mth_ovd_max", loa_lst_24mth_ovd_max[0]);

        // 贷款当月应还款汇总        -> sum(本月应还款)
        jsonData.put("mth_rep_sum", mth_rep_sum[0]);
        // 贷款(非月还)本金余额汇总        ->  sum(还款方式!=按月归还 的 本金余额)
        jsonData.put("non_mth_amt_sum", non_mth_amt_sum[0]);

        // 关注贷款笔数汇总         -> sum(case when 五级分类 = "关注" then 1 else 0 end)
        jsonData.put("gz_loa_cnt", gz_loa_cnt[0]);
        // 次级贷款笔数汇总         -> sum(case when 五级分类 = "次级" then 1 else 0 end)
        jsonData.put("cj_loa_cnt", cj_loa_cnt[0]);
        // 可疑贷款笔数汇总         -> sum(case when 五级分类 = "可疑" then 1 else 0 end)
        jsonData.put("ky_loa_cnt", ky_loa_cnt[0]);
        // 损失贷款笔数汇总         -> sum(case when 五级分类 = "损失" then 1 else 0 end)
        jsonData.put("ss_loa_cnt", ss_loa_cnt[0]);
        // 呆账贷款笔数汇总         -> sum(case when 五级分类 = "呆账" then 1 else 0 end)
        jsonData.put("dz_loa_cnt", dz_loa_cnt[0]);

        // 到期日期在当日(含)前一个月内的贷款       --> [{到期日期:...,本金余额:...,贷款类型:…,账户状态:...},{},….]
        jsonData.put("lst_1mth_loa", JSON.toJSONString(lst_1mth_loa_list));
        // 到期日期在当日后一个月内的贷款          --> [{到期日期:...,本金余额:...,贷款类型:…,账户状态:...},{},….]
        jsonData.put("aft_1mth_loa", JSON.toJSONString(aft_1mth_loa_list));
        // 贷款当前逾期金额明细           -->  json形式，key1：账户状态，key2：当前逾期金额
        jsonData.put("ovd_loa_dtl", JSON.toJSONString(ovd_loa_dtl_list));


        // -------- 信贷交易信息明细        creditStructTradeDetail
        List<Object> ovd_crd_dtl_list = Lists.newArrayList();
        creditStructTradeDetail.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Map<String, Object> ovd_crd_dtl = Maps.newHashMap();
                    ovd_crd_dtl.put("账户状态", e.getAccountStatus());
                    ovd_crd_dtl.put("当前逾期金额", e.getCurrentOverdueMoney());
                    ovd_crd_dtl_list.add(ovd_crd_dtl);
                });
        // 信用卡当前逾期金额明细         -->  json形式，key1：账户状态，key2：当前逾期金额
        jsonData.put("ovd_crd_dtl", JSON.toJSONString(ovd_crd_dtl_list));


        // ------- 对外担保贷款明细  creditStructGuaranteeLoanDetail
        final Byte[] if_grt_gz_loa = {0};
        final Byte[] if_grt_cj_loa = {0};
        final Byte[] if_grt_ky_loa = {0};
        final Byte[] if_grt_ss_loa = {0};
        final Byte[] if_grt_dz_loa = {0};

        creditStructGuaranteeLoanDetail.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 账户状态：1-正常;2-关注;3-次级;4-可疑;5-损失;6-呆账;
                    Byte accountStatus = e.getAccountStatus();
                    if (null != accountStatus) {
                        if (accountStatus == 1) {

                        } else if (accountStatus == 2) {
                            if_grt_gz_loa[0] = 1;
                        } else if (accountStatus == 3) {
                            if_grt_cj_loa[0] = 1;
                        } else if (accountStatus == 4) {
                            if_grt_ky_loa[0] = 1;
                        } else if (accountStatus == 5) {
                            if_grt_ss_loa[0] = 1;
                        } else if (accountStatus == 6) {
                            if_grt_dz_loa[0] = 1;
                        }
                    }
                });
        // 为他人担保贷款状态是否有关注       -> 判断账户状态是否有“关注”，没有送0
        jsonData.put("if_grt_gz_loa", if_grt_gz_loa[0]);
        // 为他人担保贷款状态是否有次级
        jsonData.put("if_grt_cj_loa", if_grt_cj_loa[0]);
        // 为他人担保贷款状态是否有可疑
        jsonData.put("if_grt_ky_loa", if_grt_ky_loa[0]);
        // 为他人担保贷款状态是否有损失
        jsonData.put("if_grt_ss_loa", if_grt_ss_loa[0]);
        // 为他人担保贷款状态是否有呆账
        jsonData.put("if_grt_dz_loa", if_grt_dz_loa[0]);


        // ------- 信贷交易信息明细     creditStructTradeDetail
        final int[] crd_lst_12mth_ovd_sum = {0};
        final int[] crd_lst_24mth_ovd_max = {0};

        final Byte[] if_own_icbc_crd = {0};
        final Byte[] if_icbc_crd_frz = {0};
        final Byte[] if_oth_crd_frz = {0};

        creditStructTradeDetail.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 12月内累计逾期次数
                    Integer overdueTotalNumLastMonth12 = e.getOverdueTotalNumLastMonth12();
                    if (null != overdueTotalNumLastMonth12) {
                        crd_lst_12mth_ovd_sum[0] += overdueTotalNumLastMonth12;
                    }

                    // 24月内最长逾期期数
                    Integer overdueMaxNumLastMonth24 = e.getOverdueMaxNumLastMonth24();
                    if (null != overdueMaxNumLastMonth24) {
                        crd_lst_24mth_ovd_max[0] = overdueMaxNumLastMonth24 > crd_lst_24mth_ovd_max[0] ?
                                overdueMaxNumLastMonth24 : crd_lst_24mth_ovd_max[0];
                    }

                    // 是否为工行信用卡
                    Byte isIcbcCreditCard = e.getIsIcbcCreditCard();
                    // 账户状态：1-正常;2-销户;3-未激活;4-冻结;5-呆账;6-止付;
                    Byte accountStatus = e.getAccountStatus();

                    if (BaseConst.K_YORN_YES.equals(isIcbcCreditCard)) {

                        if_own_icbc_crd[0] = 1;

                        // 冻结 || 止付
                        if (accountStatus == 4 || accountStatus == 6) {
                            if_icbc_crd_frz[0] = 1;
                        }

                    } else {
                        // 冻结 || 止付
                        if (accountStatus == 4 || accountStatus == 6) {
                            if_oth_crd_frz[0] = 1;
                        }
                    }
                });
        // 信用卡近12个月累计逾期次数       -> sum(12月内累计逾期次数)
        jsonData.put("crd_lst_12mth_ovd_sum", crd_lst_12mth_ovd_sum[0]);
        // 信用卡近24个月最长逾期期数       -> max(24月内最长逾期期数)
        jsonData.put("crd_lst_24mth_ovd_max", crd_lst_24mth_ovd_max[0]);

        // 是否有工行信用卡               -> 0/1
        jsonData.put("if_own_icbc_crd", if_own_icbc_crd[0]);
        // 是否有止付冻结的工行信用卡       -> 如果有工行信用卡的账户状态为“止付、冻结”则为1，否则 0
        jsonData.put("if_icbc_crd_frz", if_icbc_crd_frz[0]);
        // 他行信用卡状态是否有止付/冻结    -> 如果有非工行信用卡的账户状态为“止付、冻结”则为1，否则 0
        jsonData.put("if_oth_crd_frz", if_oth_crd_frz[0]);

        return jsonData;
    }

}
