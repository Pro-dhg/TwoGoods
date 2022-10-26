package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopN;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularCompanyTopNMapper {

    Long countFindRankNumberByParam(@Param("queryParam") PopularCompanyTopN popularCompanyTopN, @Param("resultTable") String resultTable, @Param("easierResultTable") String easierResultTable);

    List<PopularCompanyTopN> findRankNumberByParam(@Param("queryParam") PopularCompanyTopN popularCompanyTopN, @Param("resultTable") String resultTable, @Param("easierResultTable") String easierResultTable);
}
