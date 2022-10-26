package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.ResourceVisualizedParseRealtime;
import com.yamu.data.sample.service.resources.entity.po.ResourceVisualizedParseYes;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteUserSource;
import com.yamu.data.sample.service.resources.entity.po.VisualizationHomeData;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeDataVO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeTopNTrendVO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeTopTenParse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisualizationHomeMapper {

    VisualizationHomeData queryParseTotalCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeData queryParseTotalCntToday(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO);

    VisualizationHomeData queryParseTotalCntTodayYoy(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("nowEndTime")String nowEndTime);

    VisualizationHomeData queryParseTotalCntWlan(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeData queryParseTotalCntWlanToday(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO);

    VisualizationHomeData queryParseTotalCntWlanTodayYoy(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("nowEndTime")String nowEndTime);

    VisualizationHomeData queryParseTotalCntMobile(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeData queryParseTotalCntMobileToday(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO);

    VisualizationHomeData queryParseTotalCntMobileTodayYoy(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("nowEndTime")String nowEndTime);

    VisualizationHomeData queryTopNParseTotalCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeData queryTopNParseTotalCntToday(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO);

    VisualizationHomeData queryTopNParseTotalCntTodayYoy(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("nowEndTime")String nowEndTime);

    VisualizationHomeData queryRecursionParseTrend(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeData queryRecursionParseTrendToday(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO);

    VisualizationHomeData queryRecursionParseTrendTodayYoy(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("nowEndTime")String nowEndTime);

    List<ResourceWebsiteUserSource> findUserSource(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<ResourceWebsiteUserSource> findUserSourceToday(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO);

    List<VisualizationHomeTopNTrendVO> topNTrend(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<ResourceVisualizedParseRealtime> todayParseTotal();

    ResourceVisualizedParseYes yesterdayParseTotal(@Param("parseTime")String parseTime);

    List<VisualizationHomeTopTenParse> top10WebsiteParseCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> top10WebsiteIsTodayParseCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> top10TypeParseCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> top10TypeIsTodayParseCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> top10DomainNameParseCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> top10DomainNameIsTodayParseCnt(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> answerFirstIspProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeTopTenParse answerFirstIspAll(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeTopTenParse answerFirstIspIsTodayAll(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    List<VisualizationHomeTopTenParse> answerFirstIspIsTodayProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable);

    VisualizationHomeData  topNWebsiteProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable,@Param("isAll")String isAll,@Param("topTen")String topTen);

    VisualizationHomeData  topNWebsiteIsTodayProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable,@Param("isAll")String isAll,@Param("topTen")String topTen);

    VisualizationHomeData  topNTypeProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable,@Param("isAll")String isAll,@Param("topTen")String topTen);

    VisualizationHomeData  topNTypeIsTodayProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable,@Param("isAll")String isAll,@Param("topTen")String topTen);

    VisualizationHomeData  topNDomainNameProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable,@Param("isAll")String isAll,@Param("topTen")String topTen);

    VisualizationHomeData  topNDomainNameIsTodayProportion(@Param("queryParam")VisualizationHomeDataVO vsualizationHomeDataVO,@Param("queryTable")String queryTable,@Param("isAll")String isAll,@Param("topTen")String topTen);

    VisualizationHomeDataVO getMaxTime(@Param("queryTable")String queryTable);

}
