package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class CdnServiceQualityDownLoadHlj extends CdnServiceQualityDownLoad{

    @ApiModelProperty(value = "哈尔滨")
    private String hebData;

    @ApiModelProperty(value = "齐齐哈尔")
    private String qqheData;

    @ApiModelProperty(value = "鸡西")
    private String jxData;

    @ApiModelProperty(value = "鹤岗")
    private String hgData;

    @ApiModelProperty(value = "双鸭山")
    private String sysData;

    @ApiModelProperty(value = "大庆")
    private String dqData;

    @ApiModelProperty(value = "伊春")
    private String ycData;

    @ApiModelProperty(value = "佳木斯")
    private String jmsData;

    @ApiModelProperty(value = "七台河")
    private String qthData;

    @ApiModelProperty(value = "牡丹江")
    private String mdjData;

    @ApiModelProperty(value = "黑河")
    private String hhData;

    @ApiModelProperty(value = "绥化")
    private String shData;

    @ApiModelProperty(value = "大兴安岭")
    private String dxalData;

    @ApiModelProperty(value = "吉林")
    private String jlData;

    @ApiModelProperty(value = "辽宁")
    private String lnData;
}
