package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2022/1/12
 * @DESC
 */
@Data
public class IspProportionBO {
    private BigInteger parseTotalCnt;
    private String proportion;
    private String answerFirstIsp;
}
