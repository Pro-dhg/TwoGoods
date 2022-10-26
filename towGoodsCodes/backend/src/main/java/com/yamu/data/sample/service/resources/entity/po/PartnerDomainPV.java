package com.yamu.data.sample.service.resources.entity.po;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
@Data

public class PartnerDomainPV extends BaseRptEntity {
    private Long rankNumber;
    private String businessIp;
    private String domainName;
    private String domainType;
    /**
     * 域名类型key
     */
    private Long domainTypeKey;
    private String businessName;
    private String siteName;
    /**
     * 访问量
     */
    private BigInteger pv;

    //辅助字段
    @JsonIgnore
    private String queryType = "1d";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1d";


    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_partner_domain_name_pv_";

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

    public void setDefaultTime() {
        if(StrUtil.isEmpty(this.getStartTime()) || StrUtil.isEmpty(this.getEndTime())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(ReportUtils.DEFAULT_FMT);
            Date startTime = null;
            Date endTime = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            endTime = calendar.getTime();
            calendar.add(Calendar.DATE, -6);
            startTime = calendar.getTime();
            this.setStartTime(dateFormat.format(startTime));
            this.setEndTime(dateFormat.format(endTime));
        }
    }

    private String serverNodeName;
}
