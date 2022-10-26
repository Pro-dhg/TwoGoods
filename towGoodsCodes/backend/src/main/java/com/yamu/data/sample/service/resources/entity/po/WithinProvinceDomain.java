package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

import java.math.BigInteger;

@Data
public class WithinProvinceDomain extends BaseRptEntity {
    private String businessIp;
    private String domainName;
    private BigInteger parseTotalCnt;
    private BigInteger lastParseTotalCnt;
    // 辅助字段
    private String qtype;
    private Long rankNumber;


    //辅助字段
    @JsonIgnore
    private String queryType = "1h";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1d";

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_within_domain_";
    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }
}
