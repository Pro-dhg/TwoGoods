package com.yamu.data.sample.service.resources.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.common.excel.StringUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zl.chen
 * @create 2022/10/21 14:32
 */

@ApiModel
@Data
public class CdnBusinessAssessQueryVo extends BaseRptEntity  {

    @ApiModelProperty(value = "topn")
    private Integer rankNumber;
    @ApiModelProperty(value = "厂商名称")
    private String business;
    @ApiModelProperty(value = "查询粒度")
    private String queryType = "1min";
    @ApiModelProperty(value = "资源类型")
    private String serviceProperties;
    @ApiModelProperty(value = "用户类型")
    private String userType;
    @ApiModelProperty(value = "对比规则: 1(同比)  2(环比)  3(其他时间段)")
    private Integer compareRule;

    @ApiModelProperty(value = "对比规则选择其他时间段时的开始时间")
    private String otherStartTime;
    @ApiModelProperty(value = "对比规则选择其他时间段时的结束时间")
    private String otherEndTime;

    private static final String TABLE_PREFIX = "rpt_resource_cdn_business_assess_";

    private String queryTable = TABLE_PREFIX + "1min";


    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }
    private String startTime;
    private String endTime;

}
