package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2022/1/12
 * @DESC
 */
@Data
public class TopNWebsiteExcelBO {
    private String websiteAppName;
    private BigInteger parseTotalCnt;
    private Integer rankNumber;
}
