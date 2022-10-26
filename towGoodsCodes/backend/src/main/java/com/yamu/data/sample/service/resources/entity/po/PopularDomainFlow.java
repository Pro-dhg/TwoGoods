package com.yamu.data.sample.service.resources.entity.po;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.FlowUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author getiejun
 * @date 2021/8/11
 */
@Data
public class PopularDomainFlow extends BaseRptEntity {
    private String domainName;

    private String dialDomainName;

    private String domainType;

    private Long rankNumber;

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

    private String distributeState;

    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;
    private String timeRange;

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

    public void buildConvertFlow() {
        this.parseFlowTotal = FlowUtils.flowConvert(parseFlowTotal, FlowUtils.B, FlowUtils.GB, 2);
        this.netOutParseFlowTotal = FlowUtils.flowConvert(netOutParseFlowTotal, FlowUtils.B, FlowUtils.GB, 2);
        this.netInParseFlowTotal = FlowUtils.flowConvert(netInParseFlowTotal, FlowUtils.B, FlowUtils.GB, 2);
    }

    @JsonIgnore
    public static final String CSV_NAME = "域名流量分析报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,域名,下发状态,出网流量(G),出网次数,解析次数,成功次数,成功率,总流量(G),出网率,网内流量(G),本网次数,本网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(domainName).append(",")
                .append(distributeState).append(",")
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

    //辅助字段
    @JsonIgnore
    private String sortWay;
    @JsonIgnore
    private String queryType = "1d";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1d";
    @JsonIgnore
    private String orderBy;

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_popular_domain_flow_";

    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }

    public void setDialDomainName(String dialDomainName) {
        this.dialDomainName = dialDomainName;
        if (StrUtil.isNotEmpty(dialDomainName)) {
            this.distributeState = "已下发";
        } else {
            this.distributeState = "未下发";
        }
    }

    public void setSortWay(String sortWay) {
        this.sortWay = sortWay;
        String[] ways = sortWay.split(StrUtil.UNDERLINE);
        switch (ways[0]) {
            case "1":
                if (ways[1].equals("down")) {
                    this.orderBy = "net_out_parse_flow_total desc";
                } else {
                    this.orderBy = "net_out_parse_flow_total";
                }
                break;
            case "2":
                if (ways[1].equals("down")) {
                    this.orderBy = "parse_total_cnt desc, net_out_parse_flow_total desc";
                } else {
                    this.orderBy = "parse_total_cnt, net_out_parse_flow_total";
                }
                break;
            case "3":
                if (ways[1].equals("down")) {
                    this.orderBy = "net_out_parse_total_cnt desc, net_out_parse_flow_total desc";
                } else {
                    this.orderBy = "net_out_parse_total_cnt, net_out_parse_flow_total";
                }
                break;
            case "4":
                if (ways[1].equals("down")) {
                    this.orderBy = "net_out_parse_total_cnt desc";
                } else {
                    this.orderBy = "net_out_parse_total_cnt";
                }
                break;
            case "5":
                if (ways[1].equals("down")) {
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
}
