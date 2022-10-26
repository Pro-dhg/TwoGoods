package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class CdnServiceQualityDownLoadData {

    private String parseTime;

    private String business;

    private BigInteger parseTotalCnt;

    private BigInteger sumCnt;

    private BigInteger netInParseTotalCnt;

    private BigInteger netInCityCnt;

    private BigInteger netOutCityCnt;

    private BigInteger withinCityCnt;

    private BigInteger withOutCityCnt;

    private String userCity;

    private BigInteger userCnt;

    private BigInteger answerFirstCnt;

    private String userProvince;
}
