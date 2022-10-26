package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopNDetail;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopNNetOutDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceWebsiteTopNNetOutDetailMapper {

    Long countDomainNetOutList(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail);

    List<ResourceWebsiteTopNNetOutDetail> findDomainNetOutList(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail);

    List<ResourceWebsiteTopNNetOutDetail> findDomainNetOutExcelListNoResource(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail,@Param("websiteTopNDetail") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNNetOutDetail> findDomainNetOutExcelList(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail,@Param("websiteTopNDetail") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<String> findIspOfDomainNetOut(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail);

    Long countDomainNetOutDetailList(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail);

    List<ResourceWebsiteTopNNetOutDetail> findDomainNetOutDetailList(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail);

    List<ResourceWebsiteTopNNetOutDetail> findDomainNetOutDetailExcelListNoResource(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail,@Param("websiteTopNDetail") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNNetOutDetail> findDomainNetOutDetailExcelList(@Param("queryParam") ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail,@Param("websiteTopNDetail") ResourceWebsiteTopNDetail websiteTopNDetail);
}
