package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.DateUtils;
import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
public class FocusCompanyDetail extends BaseRptEntity {

    private String companyShortName;

    private String domainName;

    private String answerFirstIp;

    private BigInteger parseTotalCnt;

    private String answerFirstIpProvince;

    private String answerFirstIpCity;

    private String answerFirstIpIsp;

    @JsonIgnore
    public static final String CSV_NAME = "重点公司资源明细";
    @JsonIgnore
    public static final String CSV_HEAD = "公司域名,目标IP,请求次数,省份,城市,运营商\n";
    public StringBuffer csvLine = new StringBuffer();

    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(domainName).append(",")
                .append(answerFirstIp).append(",")
                .append(parseTotalCnt).append(",")
                .append(answerFirstIpProvince).append(",")
                .append(answerFirstIpCity).append(",")
                .append(answerFirstIpIsp).append("\n");
        return this.csvLine.toString();
    }

    public void setDefaultTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DEFAULT_FMT);
        Date startTime = null;
        Date endTime = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        endTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        startTime = calendar.getTime();
        this.setStartTime(dateFormat.format(startTime));
        this.setEndTime(dateFormat.format(endTime));
    }

    private String serverNodeName;
}
