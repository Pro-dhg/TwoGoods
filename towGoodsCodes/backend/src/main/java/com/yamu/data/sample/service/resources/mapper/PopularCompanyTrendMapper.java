package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTrend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularCompanyTrendMapper {
    Long countFindTrendListParam(@Param("queryParam") PopularCompanyTrend popularCompanyTrend);

    List<PopularCompanyTrend> findTrendListByParam(@Param("queryParam") PopularCompanyTrend popularCompanyTrend);

    List<PopularCompanyTrend> findTrendReportGroupByIspByParam(@Param("queryParam") PopularCompanyTrend popularCompanyTrend);

    List<PopularCompanyTrend> findTrendReportGroupByParseByParam(@Param("queryParam") PopularCompanyTrend popularCompanyTrend);

    Long countFindTrendListParamAll(@Param("queryParam") PopularCompanyTrend popularCompanyTrend);

    List<PopularCompanyTrend> findTrendListByParamAll(@Param("queryParam") PopularCompanyTrend popularCompanyTrend);

}
