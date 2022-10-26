package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceWebsiteUserSourceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceWebsiteTopNDetailMapper {

    Long countFindTrendListParam(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findTrendListByParam(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findNowTrendListByParam(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findLastTrendListByParam(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail, @Param("websites") List<String> websites);

    List<ResourceWebsiteTopNDetail> findTrendReportGroupByIspByParam(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findTrendReportGroupByParseByParam(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail,@Param("isTopN") boolean isTopN);

    List<ResourceWebsiteTopNDetail> queryNetInParseGroupByDomainType(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    Long countFindTrendListParamAll(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findTrendListByParamAll(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findNowTrendListByParamAll(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceWebsiteTopNDetail> findLastTrendListByParamAll(@Param("queryParam") ResourceWebsiteTopNDetail websiteTopNDetail, @Param("websites") List<String> websites);

    List<ResourceWebsiteUserSource> findUserSource(@Param("queryParam") ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO);

    List<ResourceWebsiteCdnReport> findCdnReport(@Param("queryParam") ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteUserSourceVO);

    List<ResourceWebsiteCdnReport> findCdnReport2(@Param("queryParam") ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteUserSourceVO);

    Long countCdnReportlList(@Param("queryParam") ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteUserSourceVO);

    List<ResourceWebsiteUserSource> findUserSourceExcelNoResource(@Param("queryParam") ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO);

    List<ResourceWebsiteUserSource> findUserSourceExcel(@Param("queryParam") ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO,@Param("websiteTopNDetail") ResourceWebsiteTopNDetail websiteTopNDetail);

    List<ResourceDistributionProvinceData> resourceDistributionProvince(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO,@Param("queryTable") String queryTable);

    List<ResourceDistributionProvinceDetail> resourceDistributionProvinceDetail(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    Long getIpDetailTotal(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    List<ResourceIpDetailData> getIpDetailList(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    List<ResourceIpDetailData> getIpDetailExcelList(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable,@Param("websiteTopNDetail") ResourceWebsiteTopNDetail websiteTopNDetail);

}
