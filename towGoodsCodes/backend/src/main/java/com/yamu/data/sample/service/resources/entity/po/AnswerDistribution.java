package com.yamu.data.sample.service.resources.entity.po;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class AnswerDistribution {

    private Long rankNumber;

    private String businessIp;

    private String districtsCode;

    private String userType;

    private String country;

    private String province;

    private String city;

    private String district;

    private String isp;

    private Date parseTime;

    private String answerFirstIpDistrictsName;

    private BigInteger parseTotalCnt;

    private BigInteger successCnt;

    private BigInteger failCnt;

    private String ispCode;
}
