package com.yamu.data.sample.service.resources.entity.po;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.FlowUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author wanghe
 * @Date 2021/8/24
 * @DESC
 */
@Data
public class PopularDomainCnameFlow extends BaseRptEntity {
    /**
     * table 前缀.
     */
    private static final String TABLE_PREFIX = "rpt_resource_popular_domain_cname_";

    /**
     * cname表名.
     */
    private static final String CNAME_TABLE_PREFIX = "rpt_resource_popular_domain_cname_detail_";

    /**
     * 排序方式.
     */
    private static final String ORDER = "down";

    /**
     * cname表名.默认查1天.
     */
    private String cnameQueryTable = CNAME_TABLE_PREFIX + "1d";


    private String domainName;

    /**
     * 波测下发域名.
     */
    private String dialDomainName;

    /**
     * 域名别名.
     */
    private String cname;

    /**
     * 域名和别名拼接,用于导出别名子表.
     */
    private List<String> domainNameCnameList;

    /**
     * 域名别名.
     */
    private List<String> cnameList;

    private String domainType;

    private Integer domainTypeKey;

    private Long rankNumber;

    /**
     * 下发状态.
     */
    private String distributeState;

    private BigInteger parseTotalCnt;

    private BigInteger parseSuccessCnt;

    private BigDecimal parseFlowTotal;

    private BigInteger netOutParseTotalCnt;

    private BigDecimal netOutParseFlowTotal;

    private BigInteger netInParseTotalCnt;

    private BigDecimal netInParseFlowTotal;

    private BigInteger withinParseTotalCnt;

    private BigInteger withoutParseTotalCnt;

    private BigInteger cdnParseTotalCnt;

    private BigInteger idcParseTotalCnt;

    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;
    private String timeRange;
    /**
     * 出网流量占比.
     */
    private Double netOutFlowRate;

    /**
     * 计算比率
     */
    public void buildRate() {
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, parseTotalCnt);
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, parseTotalCnt);
        this.parseInRate = ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt);
        this.parseOutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt);
    }

    /**
     * 计算出网流量占比.
     */
    public void buildNetOutRate() {
        this.netOutFlowRate = ReportUtils.buildRatioBase(netOutParseFlowTotal, parseFlowTotal);
    }

    public void buildConvertFlow() {
        this.parseFlowTotal = FlowUtils.flowConvert(parseFlowTotal, FlowUtils.B, FlowUtils.GB, 2);
        this.netOutParseFlowTotal = FlowUtils.flowConvert(netOutParseFlowTotal, FlowUtils.B, FlowUtils.GB, 2);
        this.netInParseFlowTotal = FlowUtils.flowConvert(netInParseFlowTotal, FlowUtils.B, FlowUtils.GB, 2);
    }

    @JsonIgnore
    public static final String CSV_NAME = "特定域名CNAME流量分析报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,域名,出网流量(G),出网次数,解析次数,成功次数,成功率,总流量(G),出网率,网内流量(G),本网次数,本网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(domainName).append(",")
                .append(netOutParseFlowTotal).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(parseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(parseFlowTotal).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(netInParseFlowTotal).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseInRate, 2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseOutRate, 2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",").append("\n");
        return this.csvLine.toString();
    }

    /**
     * 排序方式(辅助字段).
     */
    @JsonIgnore
    private String sortWay;
    @JsonIgnore
    private String queryType = "1d";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1d";
    @JsonIgnore
    private String orderBy;


    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
            this.cnameQueryTable = CNAME_TABLE_PREFIX + queryType;
        }
    }

    public void setCname(String cname) {
        if (StringUtils.isNotEmpty(cname)) {
            this.cnameList = Arrays.stream(cname.split(";")).distinct().collect(Collectors.toList());
            this.cname = Arrays.stream(cname.split(";")).distinct().collect(Collectors.joining(";"));
        }
    }


    /**
     * 拼接域名和别名组成新字段为下载子表提供条件.
     */
    public void buildDomainNameCnameList() {
        this.domainNameCnameList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(domainName) && null != cnameList && cnameList.size() > 0) {
            this.domainNameCnameList = cnameList.stream().map(cname -> domainName + cname).collect(Collectors.toList());
        }
    }

    /**
     * 排序方式.
     *
     * @param sortWay
     */
    public void setSortWay(String sortWay) {
        this.sortWay = sortWay;
        String[] ways = sortWay.split(StrUtil.UNDERLINE);
        switch (ways[0]) {
            case "1":
                if (ORDER.equals(ways[1])) {
                    this.orderBy = "net_out_parse_flow_total desc";
                } else {
                    this.orderBy = "net_out_parse_flow_total";
                }
                break;
            case "2":
                if (ORDER.equals(ways[1])) {
                    this.orderBy = "parse_total_cnt desc, net_out_parse_flow_total desc";
                } else {
                    this.orderBy = "parse_total_cnt, net_out_parse_flow_total";
                }
                break;
            case "3":
                if (ORDER.equals(ways[1])) {
                    this.orderBy = "net_out_parse_total_cnt desc, net_out_parse_flow_total desc";
                } else {
                    this.orderBy = "net_out_parse_total_cnt, net_out_parse_flow_total";
                }
                break;
            case "4":
                if (ORDER.equals(ways[1])) {
                    this.orderBy = "net_out_parse_total_cnt desc";
                } else {
                    this.orderBy = "net_out_parse_total_cnt";
                }
                break;
            case "5":
                if (ORDER.equals(ways[1])) {
                    this.orderBy = "parse_total_cnt desc";
                } else {
                    this.orderBy = "parse_total_cnt";
                }
                break;
            default:
                this.orderBy = "net_out_parse_flow_total desc";
                break;
        }

    }

    private String serverNodeName;

    private String companyShortName;

    private String websiteAppName;
}
