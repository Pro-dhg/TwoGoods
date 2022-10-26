package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessAssessQueryVo;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessAssessRespVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zl.chen
 * @create 2022/10/21 17:37
 */

@Repository
public interface CdnBusinessAssessMapper {

    List<CdnBusinessAssessRespVo> tableDetail(@Param("query") CdnBusinessAssessQueryVo queryVo,@Param("queryTable") String queryTable);
    Long getCount(@Param("query") CdnBusinessAssessQueryVo queryVo,@Param("queryTable") String queryTable);

    List<CdnBusinessAssessRespVo> compareTableList(@Param("query") CdnBusinessAssessQueryVo queryVo, @Param("list") List<String> collect,@Param("queryTable") String queryTable);

    List<CdnBusinessAssessRespVo> scoreTrend(@Param("query") CdnBusinessAssessQueryVo query,@Param("queryTable") String queryTable);

    List<CdnBusinessAssessRespVo> indicatTrend(@Param("query") CdnBusinessAssessQueryVo queryVo,@Param("queryTable") String queryTable);

    CdnBusinessAssessRespVo rader(@Param("query") CdnBusinessAssessQueryVo queryVo,@Param("queryTable") String queryTable);

    List<Map<String, Double>> scoreCityTrend(@Param("query") CdnBusinessAssessQueryVo queryVo,@Param("queryTable") String queryTable);
}
