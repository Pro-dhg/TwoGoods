package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.WithinProvinceDomain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithinProvinceDomainMapper {

//    Long countFindTrendParam(@Param("queryParam") WithinProvinceDomain withinProvinceCount);

    List<WithinProvinceDomain> findWithinProvinceDomainTopNByParam(@Param("queryParam") WithinProvinceDomain withinProvinceParseTopN);
}
