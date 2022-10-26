package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.common.excel.StringUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author Zhang Yanping
 * @Date 2021/8/26
 * @DESC
 */

@Data
public class DomainCNameRelationDetailSelect extends BaseRptEntity {

    /**
     * 查询时间
     */
    @JsonIgnore
    private String queryTime;
    /**
     * 域名
     */
    private String domainName;
    /**
     * 别名
     */
    private String cname;
    /**
     * 解析次数
     */
    private BigInteger parseTotalCnt;
    /**
     * A记录
     */
    private String aRecord;
    /**
     * 4A记录
     */
    private String aaaaRecord;
    /**
     * 是否有变化
     */
    private String change;
    /**
     * TopN
     */
    @JsonIgnore
    private Long rankNumber;
    /**
     * 格式化出来上一查询时间
     */
    @JsonIgnore
    private String lstQueryTime;


    private static final String TABLE_PREFIX = "rpt_resource_cname_relation_detail_";

    /**
     * 辅助字段用来决定查询的表
     */
    @JsonIgnore
    private String queryType = "1d";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + queryType;

    /**
     * 拼出 queryTable
     *
     * @param queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }

}
