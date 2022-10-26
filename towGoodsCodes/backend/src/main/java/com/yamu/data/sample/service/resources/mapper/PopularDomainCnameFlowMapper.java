package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PopularDomainCnameFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/8/24
 * @DESC
 */
public interface PopularDomainCnameFlowMapper {
    /**
     * 明细表数据
     *
     * @param popularDomainCnameFlow
     * @return
     */
    List<PopularDomainCnameFlow> queryGroupByDomainName(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);

    /**
     * cname明细表total
     *
     * @param popularDomainCnameFlow
     * @return
     */
    Long queryCountGroupByCName(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);

    /**
     * cname明细表数据
     *
     * @param popularDomainCnameFlow
     * @return
     */
    List<PopularDomainCnameFlow> queryGroupByCName(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);

    /**
     * 明细表total
     *
     * @param popularDomainCnameFlow
     * @return
     */
    Long queryCountGroupByDomainName(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);

    /**
     * 根据时间计算趋势.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    List<PopularDomainCnameFlow> queryGroupByParseTime(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);

    /**
     * 查询topn域名.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    List<String> topDomain(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);


    /**
     * 通过域名和别名联合搜索别名访问量数据.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    List<PopularDomainCnameFlow> queryGroupByDomainNameCname(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow);

    /**
     * 通过域名查询子表流量
     *
     * @param popularDomainCnameFlow
     * @param domainNames
     * @return
     */
    List<PopularDomainCnameFlow> queryCnameFlowByDomainName(@Param("queryParam") PopularDomainCnameFlow popularDomainCnameFlow,
                                                            @Param("domainNames") String domainNames);
}
