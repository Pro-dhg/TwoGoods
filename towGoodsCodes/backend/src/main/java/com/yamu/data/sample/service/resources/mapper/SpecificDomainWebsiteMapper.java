package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificWebsiteUserSourceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/7/22
 * @DESC
 */
@Repository
public interface SpecificDomainWebsiteMapper {
    /**
     * 按照时间段查询明细表.
     *
     * @param domainWebsiteDetail
     * @return
     */
    List<SpecificDomainWebsiteDetail> queryDataGroupByWebsiteByParam(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    /**
     * 按照时间段查询明细表total.
     *
     * @param domainWebsiteDetail
     * @return
     */
    Long countQueryDataGroupByWebsiteByParam(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    /**
     * queryDataGroupByParseTimeByParam.
     *
     * @param domainWebsiteDetail
     * @return
     */
    List<SpecificDomainWebsiteDetail> queryDataGroupByParseTimeByParam(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail,@Param("isTopN") boolean isTopN);

    /**
     * findTrendReportGroupByIspByParam.
     *
     * @param domainWebsiteDetail
     * @return
     */
    List<SpecificDomainWebsiteDetail> findTrendReportGroupByIspByParam(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    /**
     * 按照时间粒度查询明细表.
     *
     * @param domainWebsiteDetail
     * @return
     */
    List<SpecificDomainWebsiteDetail> queryDataGroupByParseTimeAndWebsiteByParam(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    /**
     * 按照时间粒度查询明细表total.
     *
     * @param domainWebsiteDetail
     * @return
     */
    Long countQueryDataGroupByParseTimeAndWebsiteByParam(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    /**
     * 按照时间段查询TopN排名趋势.
     *
     * @param domainWebsiteDetail
     * @param websites
     * @return
     */
    List<SpecificDomainWebsiteDetail> queryRankNumberGroupByWebsite(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail, @Param("websites") List<String> websites);

    /**
     * 按照时间粒度查询TopN排名趋势.
     *
     * @param domainWebsiteDetail
     * @param websites
     * @return
     */
    List<SpecificDomainWebsiteDetail> queryRankNumberGroupByWebsiteByTime(@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail, @Param("websites") List<String> websites);

    List<ResourceWebsiteUserSource> findUserSource(@Param("queryParam") ResourceSpecificWebsiteUserSourceVO resourceSpecificWebsiteUserSourceVO);

    List<ResourceWebsiteUserSource> findUserSourceExcel(@Param("queryParam") ResourceSpecificWebsiteUserSourceVO resourceSpecificWebsiteUserSourceVO,@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);

    List<ResourceDistributionProvinceData> resourceDistributionProvince(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    List<ResourceDistributionProvinceDetail> resourceDistributionProvinceDetail(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    Long getIpDetailTotal(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    List<ResourceIpDetailData> getIpDetailList(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable);

    List<ResourceIpDetailData> getIpDetailExcelList(@Param("queryParam") ResourceDistributionProvinceVO resourceDistributionProvinceVO, @Param("queryTable") String queryTable,@Param("domainWebsiteDetail") SpecificDomainWebsiteDetail domainWebsiteDetail);
}
