package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteDetail;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteNetOutDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificDomainWebsiteNetOutDetailMapper {

    Long countDomainNetOutList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<SpecificDomainWebsiteNetOutDetail> findDomainNetOutList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<SpecificDomainWebsiteNetOutDetail> findDomainNetOutListExcel(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail,@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    List<String> findIspOfDomainNetOut(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    Long countDomainNetOutDetailList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<SpecificDomainWebsiteNetOutDetail> findDomainNetOutDetailList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<SpecificDomainWebsiteNetOutDetail> findDomainNetOutDetailListExcel(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail,@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);
}
