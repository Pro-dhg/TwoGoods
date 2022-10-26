package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.FocusCompanyDetail;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTopN;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FocusCompanyTopNMapper {

    Long countFindRankNumberByParam(@Param("queryParam") FocusCompanyTopN focusCompanyTopN, @Param("resultTable") String resultTable, @Param("easierResultTable") String easierResultTable);

    List<FocusCompanyTopN> findRankNumberByParam(@Param("queryParam") FocusCompanyTopN focusCompanyTopN, @Param("resultTable") String resultTable, @Param("easierResultTable") String easierResultTable);


    Long countFindDetailByParam(@Param("queryParam") FocusCompanyDetail focusCompanyDetail, @Param("resultTable") String resultTable);

    List<FocusCompanyDetail> findDetailByParam(@Param("queryParam") FocusCompanyDetail focusCompanyDetail, @Param("resultTable") String resultTable);

}
