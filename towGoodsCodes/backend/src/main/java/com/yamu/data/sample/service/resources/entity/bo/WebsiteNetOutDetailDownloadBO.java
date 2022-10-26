package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author getiejun
 * @date 2021/10/29
 */
@Data
public class WebsiteNetOutDetailDownloadBO {
    private String answerFirstIp;
    private String answerFirstProvince;
    private String answerFirstCity;
    private String answerFirstIsp;
    private BigInteger parseTotalCnt;
}
