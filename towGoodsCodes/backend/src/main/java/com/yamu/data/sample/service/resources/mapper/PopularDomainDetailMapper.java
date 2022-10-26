package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularDomainDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularDomainDetailMapper {
    Long countFindDomainDetailByParam(@Param("queryParam") PopularDomainDetail popularDomainDetail, @Param("resultTable") String resultTable);

    List<PopularDomainDetail> findDomainDetailByParam(@Param("queryParam") PopularDomainDetail popularDomainDetail, @Param("resultTable") String resultTable);
}
