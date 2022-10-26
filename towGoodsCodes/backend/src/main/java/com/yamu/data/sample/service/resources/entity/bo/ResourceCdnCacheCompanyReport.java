package com.yamu.data.sample.service.resources.entity.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.common.utils.YamuUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.common.utils.OrderParseUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;

/**
 * @Author dhg
 * @Date 2022/5/26
 * @DESC
 */
@Data
public class ResourceCdnCacheCompanyReport {

    //网站名
    private String websiteAppName;

    //cdn域名解析次数
    private BigInteger cdnParseTotalCnt;

}
