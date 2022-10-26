package com.yamu.data.sample.service.resources.entity.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.common.utils.YamuUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.common.utils.OrderParseUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Author dhg
 * @Date 2022/5/26
 * @DESC
 */
@Data
@ApiModel
public class ResourceCdnCacheCompanyDetail {

    //时间
    @ApiModelProperty(value = "时间")
    protected String parseTime;

    //cdn厂商
    @ApiModelProperty(value = "cdn厂商")
    private String businessName;

    //网站名
    @ApiModelProperty(value = "网站名")
    private String websiteAppName;

    //cdn解析次数
    @ApiModelProperty(value = "cdn解析次数")
    private BigInteger cdnParseTotalCnt;

    //占比
    @ApiModelProperty(value = "占比")
    private Double cdnProportion;

}
