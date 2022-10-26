package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopNDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularCompanyDetailMapper {
    Long countFindDomainDetailByParam(@Param("queryParam") PopularCompanyTopNDetail popularCompanyTopNDetail, @Param("resultTable") String resultTable);

    List<PopularCompanyTopNDetail> findDomainDetailByParam(@Param("queryParam") PopularCompanyTopNDetail popularCompanyTopNDetail, @Param("resultTable") String resultTable);
}
