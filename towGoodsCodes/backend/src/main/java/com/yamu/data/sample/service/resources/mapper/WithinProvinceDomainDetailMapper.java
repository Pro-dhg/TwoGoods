package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.WithinProvinceDomainDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithinProvinceDomainDetailMapper {

    Long countFindTrendListParam(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findTrendListByParam(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findNowTrendListByParam(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findLastTrendListByParam(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findTrendReportGroupByIspByParam(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findTrendReportGroupByParseByParam(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    Long countFindTrendListParamAll(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findTrendListByParamAll(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findNowTrendListByParamAll(@Param("queryParam") WithinProvinceDomainDetail domainDetail);

    List<WithinProvinceDomainDetail> findLastTrendListByParamAll(@Param("queryParam") WithinProvinceDomainDetail domainDetail, @Param("websites") List<String> websites);
}
