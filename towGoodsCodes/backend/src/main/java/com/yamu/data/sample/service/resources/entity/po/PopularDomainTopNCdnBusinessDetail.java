package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
public class PopularDomainTopNCdnBusinessDetail extends BaseRptEntity {

    @ApiModelProperty(value = "topN参数")
    private Long rankNumber;

    @Size(max = 200, message = "公司名称不能超过200个字符")
    private String domainName;
    @ApiModelProperty(value = "解析时间")
    private Date parseTime;
    @ApiModelProperty(value = "cdn厂商")
    private String business;
    @ApiModelProperty(value = "地区编码")
    private String districtsCode;
    @ApiModelProperty(value = "国家")
    private String country;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "地区")
    private String district;
    @ApiModelProperty(value = "运营商")
    private String isp;
    @ApiModelProperty(value = "运营商编码")
    private String ispCode;
    @ApiModelProperty(value = "用户类型")
    private String userType;
    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
    @ApiModelProperty(value = "解析成功次数")
    private BigInteger parseSuccessCnt;
    @ApiModelProperty(value = "网内次数")
    private BigInteger netInParseTotalCnt;
    @ApiModelProperty(value = "省内次数")
    private BigInteger withinParseTotalCnt;

    // 入网率最大值
    @JsonIgnore
    private Integer netInRateMax;
    //入网率最小值
    @JsonIgnore
    private Integer netInRateMin;
    @JsonIgnore
    private String websiteType;
    @JsonIgnore
    private String qtype;
    //域名集合
    @JsonIgnore
    private List<String> domainList;
    //时间粒度
    @JsonIgnore
    private String statisticsWay;
    //返回时间列(根据时间粒度时:展示每一个时间粒度  根据时间段时:展示时间段)
    @JsonIgnore
    private String timeRange;
    @JsonIgnore
    private String queryTime;

    //辅助字段
    @JsonIgnore
    private String queryType = "1min";

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_domain_topn_cdn_business_";

    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";


    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
//    public void setQueryType(String queryType) {
//
//        this.queryType = queryType;
//        if (StringUtils.isNotEmpty(queryType)) {
//            this.queryTable = TABLE_PREFIX + queryType;
//        }
//    }

    //table 前缀
    private static final String TABLE_PREFIX_A = "rpt_resource_domain_topn_detail_";

    //辅助字段
    @JsonIgnore
    private String aQueryTable = TABLE_PREFIX_A + queryType;

    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
//    public void setAQueryTable(String queryType) {
//
//        this.queryType = queryType;
//        if (StringUtils.isNotEmpty(queryType)) {
//            this.aQueryTable = TABLE_PREFIX_A + queryType;
//        }
//    }

    @ApiModelProperty(value = "节点名")
    private String serverNodeName;
}
