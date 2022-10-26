package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PartnerDomainCnameFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/8/27
 * @DESC
 */
public interface PartnerDomainCnameFlowMapper {
    /**
     * 明细表数据total.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    Long countQueryGroupByDomainName(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * 明细表数据.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    List<PartnerDomainCnameFlow> queryGroupByDomainName(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * cname明细表total
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    Long queryCountGroupByCName(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * cname明细表数据
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    List<PartnerDomainCnameFlow> queryGroupByCName(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * 根据时间计算趋势.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    List<PartnerDomainCnameFlow> queryGroupByParseTime(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * 查询topn域名.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    List<String> topDomain(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * 通过域名和别名联合搜索别名访问量数据.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    List<PartnerDomainCnameFlow> queryGroupByDomainNameCname(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow);

    /**
     * 通过域名查询子表流量
     *
     * @param partnerDomainCnameFlow
     * @param domainNames
     * @return
     */
    List<PartnerDomainCnameFlow> queryCnameFlowByDomainName(@Param("queryParam") PartnerDomainCnameFlow partnerDomainCnameFlow,
                                                            @Param("domainNames") String domainNames);
}
