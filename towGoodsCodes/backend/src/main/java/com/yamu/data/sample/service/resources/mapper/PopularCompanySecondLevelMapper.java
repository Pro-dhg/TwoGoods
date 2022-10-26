package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularCompanySecondLevel;
import com.yamu.data.sample.service.resources.entity.vo.SecondDomainServerVO;
import com.yamu.data.sample.service.resources.entity.vo.SecondDomainTableListVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2022/3/16
 * @DESC
 */
@Repository
public interface PopularCompanySecondLevelMapper {
    //导出，时间段
    List<PopularCompanySecondLevel> queryDataByTimeParamToDownload(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //导出
    List<PopularCompanySecondLevel> queryDataByQueryTimeParamToDownload(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);

    List<PopularCompanySecondLevel> findTrendReportGroupByIspByParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);

    List<PopularCompanySecondLevel> queryLastTimeDataGroupByWebsiteByParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);

    //按时间段查总表
    List<SecondDomainTableListVO> queryDataGroupByTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间段查子表
    List<PopularCompanySecondLevel> querySecondDataGroupByTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间粒度查总表
    List<SecondDomainTableListVO> queryDataGroupByQueryTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间粒度查子表
    List<PopularCompanySecondLevel> querySecondDataGroupByQueryTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间段汇聚total
    Long countQueryDataGroupByTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间段汇聚子表total
    Long countQuerySecondDataGroupByTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间粒度汇聚总表total
    Long countQueryDataGroupByQueryTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);
    //按时间粒度汇聚子表total
    Long countQuerySecondDataGroupByQueryTimeParam(@Param("queryParam") PopularCompanySecondLevel companySecondLevel);

    Long nodeServerDetailCount(@Param("queryParam") PopularCompanySecondLevel queryParam);
    List<SecondDomainServerVO> nodeServerDetail(@Param("queryParam") PopularCompanySecondLevel queryParam);

}
