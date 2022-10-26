package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopN;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularDomainTopNMapper {

    Long countFindRankNumberByParam(@Param("queryParam") PopularDomainTopN popularDomainTopN, @Param("resultTable") String resultTable);

    List<PopularDomainTopN> findRankNumberByParam(@Param("queryParam") PopularDomainTopN popularDomainTopN, @Param("resultTable") String resultTable);

    List<PopularDomainTopN> findLastRankNumber(@Param("queryParam") PopularDomainTopN popularDomainTopN, @Param("resultTable") String resultTable, @Param("domainList") List list);


    List<PopularDomainTopN> download(@Param("queryParam") PopularDomainTopN popularDomainTopN, @Param("resultTable") String resultTable);
}
