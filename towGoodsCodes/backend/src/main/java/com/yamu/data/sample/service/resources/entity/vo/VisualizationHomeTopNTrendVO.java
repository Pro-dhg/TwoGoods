package com.yamu.data.sample.service.resources.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigInteger;
import java.util.Date;

@Data
@ApiModel
public class VisualizationHomeTopNTrendVO {

    @ApiModelProperty(value = "时间")
    private Date parseTime;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "成功次数")
    private BigInteger parseSuccessCnt;

    @ApiModelProperty(value = "失败次数")
    private BigInteger parseFailCnt;

    @ApiModelProperty(value = "ipv4次数")
    @JsonProperty("aRecordParseTotalCnt")
    private BigInteger aRecordParseTotalCnt;

    @ApiModelProperty(value = "ipv6次数")
    @JsonProperty("aaaaRecordParseTotalCnt")
    private BigInteger aaaaRecordParseTotalCnt;

    @ApiModelProperty(value = "本网次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "网外次数")
    private BigInteger netOutParseTotalCnt;

    @ApiModelProperty(value = "时间格式化")
    private String time;

}
