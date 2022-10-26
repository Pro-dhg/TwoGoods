package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class WebsiteDomainNameExcelBO {
    private String domainName;
    private BigInteger parseTotalCnt;
}
