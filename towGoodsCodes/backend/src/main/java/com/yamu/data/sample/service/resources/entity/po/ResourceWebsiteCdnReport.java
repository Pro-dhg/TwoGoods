package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dhg
 * Date 2022-05-11
 */
@Data
@ApiModel
public class ResourceWebsiteCdnReport {

    @JsonIgnore
    private BigInteger cdnParseTotalCnt;

    public ResourceWebsiteCdnReport() {
    }

    public ResourceWebsiteCdnReport(String cdnBusiness, BigInteger cdnResponseCnt, Double cdnResponseProportion) {
        this.cdnBusiness = cdnBusiness;
        this.cdnResponseCnt = cdnResponseCnt;
        this.cdnResponseProportion = cdnResponseProportion;
    }

    @ApiModelProperty(value = "cdn厂商")
    private String cdnBusiness;
    @ApiModelProperty(value = "cdn应答次数")
    private BigInteger cdnResponseCnt;
    @ApiModelProperty(value = "应答占比")
    private Double responseProportion;
    @ApiModelProperty(value = "cdn应答占比")
    private Double cdnResponseProportion;
    @ApiModelProperty(value = "成功次数")
    private BigInteger successCnt;
    @ApiModelProperty(value = "成功率")
    private Double successRate;
    @ApiModelProperty(value = "本网次数")
    private BigInteger netInCnt;
    @ApiModelProperty(value = "本网率")
    private Double netInRate;
    @ApiModelProperty(value = "本省次数")
    private BigInteger withinCnt;
    @ApiModelProperty(value = "本省率")
    private Double withinRate;
    @ApiModelProperty(value = "应答次数")
    private BigInteger responseCnt;

}
