package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CompanyExcelBO {
    private String companyShortName;
    private BigInteger parseTotalCnt;
}
