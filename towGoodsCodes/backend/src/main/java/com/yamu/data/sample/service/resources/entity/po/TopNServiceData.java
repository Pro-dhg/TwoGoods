package com.yamu.data.sample.service.resources.entity.po;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/09/22
 */

@Data
public class TopNServiceData {

    private String domainName;

    private String companyShortName;

    private BigInteger parseTotalCnt;

    private BigInteger netInParseTotalCnt;

    private String serviceDomainName;

    private String business;
}
