package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceTopNTypeUserSourceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/23
 * @DESC
 */
@Repository
public interface PopularDomainTopNTypeMapper {

    List<PopularDomainTopNType> findTrendReportGroupByIspByParam(@Param("queryParam") PopularDomainTopNType PopularDomainTopNType);

    List<PopularDomainTopNType> findTrendReportGroupByIspByParamEvery(@Param("queryParam") PopularDomainTopNType PopularDomainTopNType);


    List<PopularDomainTopNType> queryDataGroupByWebsiteTypeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail);

    Long countQueryDataGroupByWebsiteTypeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail);

    List<PopularDomainTopNType> queryDataGroupByParseTimeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail);


    List<PopularDomainTopNType> queryDataGroupByParseTimeAndWebsiteTypeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail);

    Long countQueryDataGroupByParseTimeAndWebsiteTypeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail);

    List<PopularDomainTopNType> queryLastTimeDataGroupByWebsiteTypeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail, @Param("queryList") List<String> websiteTypeList);

    List<PopularDomainTopNType> queryLastTimeDataGroupByParseTimeAndWebsiteTypeByParam(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail, @Param("queryList") List<String> websiteTypes);

    List<PopularDomainCdnReport> findCdnReport(@Param("queryParam") PopularDomainTopNCdnBusinessDetail resourceWebsiteUserSourceVO);

    Long countCdnReportlList(@Param("queryParam") PopularDomainTopNCdnBusinessDetail resourceWebsiteUserSourceVO);

    List<PopularDomainTopNType> queryNetInParseGroupByDomainType(@Param("queryParam") PopularDomainTopNType domainNameWebsiteDetail);

    List<String> queryDataGroupByOperatorByParam(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    Long countQueryDataGroupByWebsiteTypeDetailByParam(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    List<PopularDomainTopNTypeDetail> queryDataGroupByWebsiteTypeDetailByParam(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    List<PopularDomainTopNTypeDetail> queryDataGroupByWebsiteTypeDetailByParamExcel(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    Long countQueryDataGroupByOutDomainTableDetailByParam(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    List<PopularDomainTopNTypeDetail> queryDataGroupByOutDomainTableDetailByParam(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    List<PopularDomainTopNTypeDetail> queryDataGroupByOutDomainTableDetailByParamExcel(@Param("queryParam") PopularDomainTopNTypeDetail domainNameWebsiteDetail);

    List<ResourceWebsiteUserSource> findUserSource(@Param("queryParam") ResourceTopNTypeUserSourceVO resourceTopNTypeUserSourceVO);
}
