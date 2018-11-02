package com.yunche.loan.web.aspect;

import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.mapper.FinancialProductDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanProcessLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;

@Aspect
@Component
public class FunctionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(FunctionTimeAspect.class);

    @Autowired
    private LoanProcessLogService loanProcessLogService;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;


    /**
     * @param point
     */
    @Around("@annotation(com.yunche.loan.config.anno.FunctionTime)")

    public Object around(ProceedingJoinPoint point) throws Throwable {


        Long orderId = null;

        Object[] args = point.getArgs();

        for (int i = 0; i < args.length; i++) {

            Object arg = args[i];

            if (null != arg && (arg instanceof Long) && arg.toString().length() >= 19) {

                orderId = (Long) arg;
            }
        }


        if (checkCreditTime(orderId)) {

            return do_method_new(point);

        } else {

            Object result = point.proceed();

            return result;
        }

    }

    private Object do_method_new(ProceedingJoinPoint point) {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        String methodName = method.getName();
        String newMethodName = methodName + "New";

        Class<?> clazz = method.getDeclaringClass();

//        Class<? extends LoanQueryDOMapper> clazz = loanQueryDOMapper.getClass();


        Object result = null;
        try {


            Method newMethod = clazz.getMethod(newMethodName, method.getParameterTypes());

            Object[] args = point.getArgs();


            if (clazz.equals(LoanQueryDOMapper.class)) {

                result = newMethod.invoke(loanQueryDOMapper, args);

            } else if (clazz.equals(FinancialProductDOMapper.class)) {

                result = newMethod.invoke(financialProductDOMapper, args);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    public static void main(String[] args) {

        Class<LoanQueryDOMapper> clazz = LoanQueryDOMapper.class;

        if (clazz.equals(LoanQueryDOMapper.class)) {

            System.out.println(1);

        } else if (clazz.equals(FinancialProductDOMapper.class)) {

            System.out.println(2);
        }
    }


    boolean checkCreditTime(Long orderId) {
        // true：新公式  false：老公式
        boolean flag = true;

        if (orderId == null) {
            return flag;
        }
        try {
            // 需要额外判断一下该订单的征信申请时间，如果是2018年11月1日之前申请的，则使用老版公式
            LoanProcessLogDO loanProcessLog = loanProcessLogService.getLoanProcessLog(orderId, CREDIT_APPLY.getCode());
            if (loanProcessLog != null) {
                if (loanProcessLog.getCreateTime().before(DateUtil.getDate("20181101"))) {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } catch (Exception e) {
            return false;
        }
        return flag;
    }
}
