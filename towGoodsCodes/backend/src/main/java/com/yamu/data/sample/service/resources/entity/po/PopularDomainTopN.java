package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

import java.math.BigInteger;

@Data
public class PopularDomainTopN extends BaseRptEntity {

    private String userType;

    private String businessIp;

    private String domainName;

    private BigInteger parseTotalCnt;

    private BigInteger lastParseTotalCnt;

    private BigInteger parseVariationCnt;

    private Long rankNumber;

    private Long lastRankNumber;

    @JsonIgnore
    protected String lastStartTime;
    @JsonIgnore
    protected String LastEndTime;

    @JsonIgnore
    public static final String CSV_NAME = "热点域名TopN变化分析报表";
    @JsonIgnore
    public static final String CSV_HEAD = "域名,当前排名,当前解析次数,上个时间段排名,上个时间段解析次数,变化值\n";
    public StringBuffer csvLine = new StringBuffer();

    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        String num = "";
        if ((lastRankNumber != null) && (lastRankNumber != 0L)) {
            num = lastRankNumber.toString();
        }
        this.csvLine
                .append(domainName).append(",")
                .append(rankNumber).append(",")
                .append(parseTotalCnt).append(",")
                .append(num).append(",")
                .append(lastParseTotalCnt).append(",")
                .append(parseVariationCnt).append("\n");
        return this.csvLine.toString();
    }

    private String serverNodeName;
}
