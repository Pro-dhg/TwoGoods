package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.common.excel.StringUtils;
import com.yamu.data.sample.common.utils.YamuUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;

import java.math.BigInteger;

/**
 * @Author: ZhangYanping
 * @Date: 2021/7/5 23:41
 * @Desc: 特定域名TopN网站分析
 */
@Data
public class PartnerDomainTopNWebsite extends BaseRptEntity {

    /**
     * 统计方式
     */
    private String queryMethod;
    /**
     * topN
     */
    private Long rankNumber;

    /**
     * 网站名称（模糊）
     */
    private String websiteName;
    /**
     * 分类
     */
    private Long type;
    /**
     * 解析次数
     */
    private BigInteger parseTotalCnt;
    /**
     * 成功次数
     */
    private BigInteger parseSuccessCnt;
    /**
     * 成功率
     */
    private Double successRate;
    /**
     * 出网次数
     */
    private BigInteger netOutParseTotalCnt;
    /**
     * 外网率
     */
    private Double netOutRate;
    /**
     * 网内次数
     */
    private BigInteger netInParseTotalCnt;
    /**
     * 本网率
     */
    private Double netInRate;
    /**
     * 本省次数
     */
    private BigInteger withinParseTotalCnt;
    /**
     * 本省率
     */
    private Double withinRate;
    /**
     * 省外次数
     */
    private BigInteger withoutParseTotalCnt;
    /**
     * 出省率
     */
    private Double withoutRate;
    /**
     * cdn次数
     */
    private BigInteger cdnParseTotalCnt;
    /**
     * idc次数
     */
    private BigInteger idcParseTotalCnt;


    private static final String TABLE_PREFIX = "rpt_resource_focus_company_trend_";

    /**
     * 辅助字段用来决定查询的表
     */
    @JsonIgnore
    private String queryType = "";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + queryType;

    /**
     * 拼出 queryTable
     * @param queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }

    /**
     * 计算比率
     */
    public void buildRate() {
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, parseTotalCnt);
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, parseTotalCnt);
        this.withinRate = ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt);
        this.withoutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt);
    }

    @JsonIgnore
    public static final String CSV_NAME = "TopN网站解析明细报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,网站名称,分类,解析次数,成功次数,成功率,出网次数,外网率,网内次数,本网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(YamuUtils.getSimpleDateFormat().format(parseTime)).append(",")
                .append(websiteName).append(",")
                .append(type).append(",")
                .append(parseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate,2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate,2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate,2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withinRate,2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withoutRate,2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",").append("\n");
        return this.csvLine.toString();
    }
}
