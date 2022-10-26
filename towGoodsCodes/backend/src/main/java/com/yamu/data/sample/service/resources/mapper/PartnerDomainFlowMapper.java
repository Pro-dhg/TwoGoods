package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PartnerDomainFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/8/18
 * @DESC
 */
@Repository
public interface PartnerDomainFlowMapper {
    /**
     * 明细表total
     *
     * @param partnerDomainFlow
     * @return
     */
    Long countQueryGroupByDomainName(@Param("queryParam") PartnerDomainFlow partnerDomainFlow);

    /**
     * 根据domain进行group by明细.
     *
     * @param partnerDomainFlow
     * @return
     */
    List<PartnerDomainFlow> queryGroupByDomainName(@Param("queryParam") PartnerDomainFlow partnerDomainFlow);

    /**
     * 趋势分析.
     *
     * @param partnerDomainFlow
     * @return
     */
    List<PartnerDomainFlow> queryGroupByParseTime(@Param("queryParam") PartnerDomainFlow partnerDomainFlow);

    /**
     * 获取TopN域名
     * @param partnerDomainFlow
     * @return
     */
    List<String> findTopDomain(@Param("queryParam") PartnerDomainFlow partnerDomainFlow);
}
