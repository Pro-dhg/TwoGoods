package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.StrUtils;
import org.apache.commons.lang3.StringUtils;
import com.yamu.data.sample.common.utils.YamuUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Data
public class ResourcePartnerDomain extends BaseRptEntity {
    private String userType;
    private Long domainTypeKey;
    private String srcIp;
    private String qtype;
    private Long rankNumber;
    // 汇总数据
    private BigInteger parseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger parseFailCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withOutParseTotalCnt;
    private BigInteger withInParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger cacheParseTotalCnt;
    private BigInteger idcParseTotalCnt;
    private BigInteger aRecordParseTotalCnt;

    @JsonIgnore
    private boolean otherQtype = false;
    // 辅助字段用来决定查询的表
    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";


    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_partner_domain_name_detail_";

    public void setQtype(String qtype) {
        if (StringUtils.isEmpty(qtype)) {
        } else if ("other".equals(qtype)) {
            otherQtype = true;
        } else {
            this.qtype = qtype;
        }
    }

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


    // csv内容
    public static final String CSV_NAME = "特定域名分析表";
    public static final String CSV_HEAD = "时间,解析次数,解析成功次数,成功率,出网次数,出网率, 国内次数,本网率, 本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
   @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();
@JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(YamuUtils.getSimpleDateFormat().format(parseTime)).append(",")
                .append(parseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(new BigDecimal(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt) * 100).setScale(2, RoundingMode.HALF_UP)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(new BigDecimal(ReportUtils.buildRatioBase(netOutParseTotalCnt, parseTotalCnt) * 100).setScale(2, RoundingMode.HALF_UP)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(new BigDecimal(ReportUtils.buildRatioBase(netInParseTotalCnt, parseTotalCnt) * 100).setScale(2, RoundingMode.HALF_UP)).append(",")
                .append(withInParseTotalCnt).append(",")
                .append(new BigDecimal(ReportUtils.buildRatioBase(withInParseTotalCnt, parseTotalCnt) * 100).setScale(2, RoundingMode.HALF_UP)).append(",")
                .append(withOutParseTotalCnt).append(",")
                .append(new BigDecimal(ReportUtils.buildRatioBase(withOutParseTotalCnt, parseTotalCnt) * 100).setScale(2, RoundingMode.HALF_UP)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append("\n");
        return this.csvLine.toString();
    }


// todo  之后将改部分单独用jsonobject结构进行返回吧

    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;
    private Double successRate;

    public void setRankNumber(Long rankNumber) {
        if (rankNumber == null || rankNumber == 0L) {
        } else {
            this.rankNumber = rankNumber;
        }
    }

    /**
     * 计算比率
     */
    public void buildRate() {
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt);
        this.parseInRate = ReportUtils.buildRatioBase(withInParseTotalCnt, parseTotalCnt);
        this.parseOutRate = ReportUtils.buildRatioBase(withOutParseTotalCnt, parseTotalCnt);
    }

    @JsonIgnore
    public String getCsvLineForTotal() {
        this.csvLine.setLength(0);
        csvLine
                .append(YamuUtils.getSimpleDateFormat().format(parseTime)).append(",")
                .append(parseTotalCnt).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(withInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseInRate, 2)).append(",")
                .append(withOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseOutRate, 2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",")
                .append(cacheParseTotalCnt).append("\n");
        return csvLine.toString();
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }

    private String serverNodeName;
}
