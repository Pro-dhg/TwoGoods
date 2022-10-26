package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteUserSource;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainNameWebsiteDetail;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteNetOutDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificTopNTypeUserSourceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author getiejun
 * @date 2021/7/21
 */
@Repository
public interface SpecificDomainNameWebsiteMapper {

    List<SpecificDomainNameWebsiteDetail> queryDataGroupByWebsiteTypeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    Long countQueryDataGroupByWebsiteTypeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    List<SpecificDomainNameWebsiteDetail> queryDataGroupByAllParseTimeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    List<SpecificDomainNameWebsiteDetail> queryDataGroupByEveryParseTimeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    List<SpecificDomainNameWebsiteDetail> findTrendReportGroupByIspByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    List<SpecificDomainNameWebsiteDetail> queryDataGroupByParseTimeAndWebsiteTypeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    Long countQueryDataGroupByParseTimeAndWebsiteTypeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail);

    List<SpecificDomainNameWebsiteDetail> queryLastTimeDataGroupByWebsiteTypeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail, @Param("queryList") List<String> websiteTypeList);

    List<SpecificDomainNameWebsiteDetail> queryLastTimeDataGroupByParseTimeAndWebsiteTypeByParam(@Param("queryParam") SpecificDomainNameWebsiteDetail domainNameWebsiteDetail, @Param("queryList") List<String> websiteTypes);

    List<String> findIspOfDomainNetOut(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    Long countDomainNetOutList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<SpecificDomainWebsiteNetOutDetail> findDomainNetOutList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    Long countDomainNetOutDetailList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<SpecificDomainWebsiteNetOutDetail> findDomainNetOutDetailList(@Param("queryParam") SpecificDomainWebsiteNetOutDetail websiteNetOutDetail);

    List<ResourceWebsiteUserSource> findUserSource(@Param("queryParam") ResourceSpecificTopNTypeUserSourceVO resourceSpecificTopNTypeUserSourceVO);
}
