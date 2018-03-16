package com.yunche.loan.domain.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class LoanFileParam {

    private Byte type;

    private List<MultipartFile> files;

}
