package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.ResourceCdnDistribution;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xh.wu
 * @date 2021/10/20
 */
@Repository
public interface ResourceCdnDistributionMapper {

    List<ResourceCdnDistribution> queryCompany(@Param("queryParam") ResourceCdnDistribution param);

    Long countCompany(@Param("queryParam") ResourceCdnDistribution param);

    List<ResourceCdnDistribution> queryDomainByCompany(@Param("queryParam") ResourceCdnDistribution param);

    Long countDomainByCompany(@Param("queryParam") ResourceCdnDistribution param);

    List<ResourceCdnDistribution> queryDownload(@Param("queryParam") ResourceCdnDistribution param);
}
