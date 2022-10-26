package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author dhg
 * @Date 2022/5/26
 * @DESC
 */
@Data
public class ResourceCdnCacheDomainReport {

    //网站名
    private String websiteAppName;

    //cdn域名解析次数
    private BigInteger cdnParseTotalCnt;

}
