package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author getiejun
 * @date 2021/10/29
 */
@Data
public class WebsiteNetOutDownloadBO {

    private String domainName;
    private String answerFirstIsp;
    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
}
