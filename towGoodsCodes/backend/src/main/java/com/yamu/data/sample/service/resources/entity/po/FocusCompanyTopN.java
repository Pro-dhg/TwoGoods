package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
public class FocusCompanyTopN extends BaseRptEntity {

    private String companyShortName;

    private BigInteger parseTotalCnt;

    private BigInteger lastParseTotalCnt;

    private BigInteger parseVariationCnt;

    private Long rankNumber;

    private Long lastRankNumber;

    @JsonIgnore
    public static final String CSV_NAME = "重点公司TopN变化分析报表";
    @JsonIgnore
    public static final String CSV_HEAD = "公司名,当前排名,当前解析次数,上个时间段排名,上个时间段解析次数,变化值\n";
    public StringBuffer csvLine = new StringBuffer();

    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(companyShortName).append(",")
                .append(rankNumber).append(",")
                .append(parseTotalCnt).append(",")
                .append(lastRankNumber).append(",")
                .append(lastParseTotalCnt).append(",")
                .append(parseVariationCnt).append("\n");
        return this.csvLine.toString();
    }

    public void setDefaultTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DEFAULT_FMT);
        Date startTime = null;
        Date endTime = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        endTime = calendar.getTime();
        calendar.add(Calendar.HOUR, -24);
        startTime = calendar.getTime();
        this.setStartTime(dateFormat.format(startTime));
        this.setEndTime(dateFormat.format(endTime));
    }

    private String serverNodeName;
}
