package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTrend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FocusCompanyTrendMapper {
    /**
     * 重点公司访问趋势分析: 访问重点公司趋势明细
     * @param focusCompanyTrend
     * @return
     */
    Long countTrendDetailByPage(@Param("queryParam") FocusCompanyTrend focusCompanyTrend);

    List<FocusCompanyTrend> findTrendDetailByPage(@Param("queryParam") FocusCompanyTrend focusCompanyTrend);

    /**
     * 重点公司访问趋势分析: 重点公司资源分布图表
     * @param focusCompanyTrend
     * @return
     */
    List<FocusCompanyTrend> findTrendReportGroupByIspByParam(@Param("queryParam") FocusCompanyTrend focusCompanyTrend);


    List<FocusCompanyTrend> findTrendReportGroupByParseTimeByParam(@Param("queryParam") FocusCompanyTrend focusCompanyTrend);

    Long countTrendDetailByPageAll(@Param("queryParam") FocusCompanyTrend focusCompanyTrend);

    List<FocusCompanyTrend> findTrendDetailByPageAll(@Param("queryParam") FocusCompanyTrend focusCompanyTrend);
}
