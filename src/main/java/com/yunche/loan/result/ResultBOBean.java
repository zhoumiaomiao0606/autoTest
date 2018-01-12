package com.yunche.loan.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yunche.loan.common.Code;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Data
public class ResultBOBean<T> implements Serializable {

    private static final long serialVersionUID = -2361820086956983473L;

    // 数据明细
    private T data;

    private Boolean isError;

    private Code code = Code.Success;

    private String msg;

    //　为null时,不参与序列化
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalNum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageIndex;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalPage;

    public static <T> ResultBOBean of(T data, boolean isError, Code code, String msg) {
        ResultBOBean resultBOBean = new ResultBOBean<>();
        resultBOBean.setData(data);
        resultBOBean.setIsError(isError);
        resultBOBean.setCode(code);
        resultBOBean.setMsg(msg);
        if (data instanceof Collection) {
            resultBOBean.setTotalNum(((Collection) data).size());
        }
        return resultBOBean;
    }

    public static <T> ResultBOBean<T> of(T data, boolean error, Code code, String msg, Integer totalNum, Integer pageIndex, Integer pageSize) {
        ResultBOBean resultBOBean = new ResultBOBean();
        resultBOBean.setData(data);
        resultBOBean.setIsError(error);
        resultBOBean.setCode(code);
        resultBOBean.setMsg(msg);
        resultBOBean.setTotalNum(totalNum);
        resultBOBean.setPageIndex(pageIndex);
        resultBOBean.setPageSize(pageSize);
        resultBOBean.setTotalPage((pageSize == null || pageSize == 0) ? null : (totalNum % pageSize == 0 ? totalNum / pageSize : (totalNum / pageSize + 1)));
        return resultBOBean;
    }

    public static <T> ResultBOBean<T> ofSuccess(T data) {
        return of(data, false, Code.Success, null);
    }

    public static <T> ResultBOBean<T> ofSuccess(T data, String msg) {
        return of(data, false, Code.Success, msg);
    }

    public static ResultBOBean ofError(String msg) {
        ResultBOBean resultBOBean = new ResultBOBean();
        resultBOBean.setIsError(true);
        resultBOBean.setCode(Code.Error);
        resultBOBean.setData(null);
        resultBOBean.setMsg(msg);
        return resultBOBean;
    }

}

