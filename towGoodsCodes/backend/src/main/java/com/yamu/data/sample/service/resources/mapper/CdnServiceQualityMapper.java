package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dys
 * @Date 2022/07/26
 */
@Repository
public interface CdnServiceQualityMapper {

    CdnServiceQualityData getSumData(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityData> getMap(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityData> getMapProvince(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    CdnServiceQualityCdnCoverList getCdnCoverCnt(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityCdnCoverList> getCdnServiceQualityCdnCoverList(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    CdnServiceQualityCdnCoverList getCdnCoverCntProvince(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityCdnCoverList> getCdnServiceQualityCdnCoverListProvince(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    ResponseResultsTrend getResponseResultsTrend(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityData> getRateTrend(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<ResourceCdnServerDispatchTrend> getDispatchTrend(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityDownLoadData> getDownloadData(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityDownLoadData> getDownloadDataCity(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);

    List<CdnServiceQualityDownLoadData> getDownloadDataNearProvince(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable, @Param("provinceList")List<String> provinceList);

    List<String> getTopNCdn(@Param("queryParam") CdnServiceQualityParam cdnServiceQualityParam, @Param("queryTable")String queryTable);
}
