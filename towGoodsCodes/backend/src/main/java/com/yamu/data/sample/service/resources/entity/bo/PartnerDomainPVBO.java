package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class PartnerDomainPVBO {

    //解析时间
    private String domainName;
    private String domainType;
    private String businessName;
    private String siteName;
    /**
     * 访问量
     */
    private BigInteger pv;

    /**
     * 序号
     */
    private Integer serialNumber;
}
