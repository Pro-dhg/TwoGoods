package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * @Author yuyuan.Dong
 * @Date 2022/1/13
 * @DESC
 */
@Data
public class PopularDomainNameBO {
    private Date parseTime;
    private String name;
    private BigInteger parseTotalCnt;
}
