package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainDowmloadList;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainFlowTrend;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainNetInTrend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dhg
 * @Date 2022/10/19
 */
@Repository
public interface PopularCnameDomainQueryDomainMapper {

    Long getTotal(@Param("queryParam") PopularCnameDomainQueryParam popularCnameDomainQueryParam, @Param("queryTable")String queryTable);

    List<PopularCnameDomainList> getDataList(@Param("queryParam") PopularCnameDomainQueryParam popularCnameDomainQueryParam , @Param("queryTable")String queryTable, @Param("orderByStr")String orderByStr);
    List<PopularCnameDomainFlowTrend> getFlowTrend(@Param("queryParam") PopularCnameDomainFlowTrendParam popularCnameDomainFlowTrendParam , @Param("queryTable")String queryTable);
    List<PopularCnameDomainList> getNetInTrend(@Param("queryParam") PopularCnameDomainFlowTrendParam popularCnameDomainFlowTrendParam , @Param("queryTable")String queryTable);
    List<PopularCnameDomainDowmloadList> getDownload(@Param("queryParam") PopularCnameDomainParam popularCnameDomainParam, @Param("queryTable")String queryTable);
}
