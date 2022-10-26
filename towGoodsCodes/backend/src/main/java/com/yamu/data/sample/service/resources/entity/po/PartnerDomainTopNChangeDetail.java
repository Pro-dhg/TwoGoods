package com.yamu.data.sample.service.resources.entity.po;

import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.DateUtils;
import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/7
 * @DESC
 */
@Data
public class PartnerDomainTopNChangeDetail extends BaseRptEntity {

    private String domainName;

    private String answerFirstIp;

    private String answerFirstIpDistrict;

    private String answerFirstIpIsp;

    private BigInteger parseTotalCnt;

    public void setDefaultTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DEFAULT_FMT);
        Date startTime = null;
        Date endTime = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        endTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -9);
        startTime = calendar.getTime();
        this.setStartTime(dateFormat.format(startTime));
        this.setEndTime(dateFormat.format(endTime));
    }
}
