package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.common.excel.StringUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

/**
 * @author Zhang Yanping
 * @Date 2021/8/26
 * @DESC
 */

@Data
public class DomainCNameRelationMap extends BaseRptEntity {

    /**
     * 查询时间
     */
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


    private static final String TABLE_PREFIX = "rpt_resource_cname_relation_";

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

//    private static final String DETAIL_TABLE_PREFIX = "rpt_resource_cname_relation_detail_";
//    /**
//     * 辅助字段用来决定查询的cname详情表
//     */
//    @JsonIgnore
//    private String queryDetailTable = DETAIL_TABLE_PREFIX + queryType;
//
//    public void setQueryDetailType(String queryType) {
//        this.queryType = queryType;
//        if (StringUtils.isNotEmpty(queryType)) {
//            this.queryDetailTable = DETAIL_TABLE_PREFIX + queryType;
//        }
//    }


    // csv文件名
    public static final String CSV_NAME = "域名-CNAME关系图谱";

    // csv表头
    public static final String CSV_HEAD = "时间,域名,域名所属网站,域名所属公司,别名,解析次数,A记录,AAAA记录,是否有变化\n";

    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        csvLine.append(new SimpleDateFormat("yyyy-MM-dd").format(parseTime)).append(",")
                .append(domainName).append(",")
                .append(websiteAppName).append(",")
                .append(companyShortName).append(",")
                .append(cname).append(",")
                .append(parseTotalCnt).append(",")
                .append(aRecord).append(",")
                .append(aaaaRecord).append(",")
                .append(change).append("\n");
        return csvLine.toString();
    }

    private String companyShortName;

    private String websiteAppName;

}
