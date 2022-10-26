package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularDomainFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author getiejun
 * @date 2021/8/11
 */
@Repository
public interface PopularDomainFlowMapper {

    Long countQueryGroupByDomainName(@Param("queryParam") PopularDomainFlow popularDomainFlow);

    List<PopularDomainFlow> queryGroupByDomainName(@Param("queryParam") PopularDomainFlow popularDomainFlow);

    List<PopularDomainFlow> queryGroupByParseTime(@Param("queryParam") PopularDomainFlow popularDomainFlow);

    List<String> findTopDomain(@Param("queryParam") PopularDomainFlow popularDomainFlow);
}
