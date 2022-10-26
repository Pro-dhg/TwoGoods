package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNChangeDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/7
 * @DESC
 */
@Repository
public interface PartnerDomainTopNChangeDetailMapper {
    Long countFindDomainDetailByParam(@Param("queryParam") PartnerDomainTopNChangeDetail domainTopNChangeDetail, @Param("resultTable") String resultTable);

    List<PartnerDomainTopNChangeDetail> findDomainDetailByParam(@Param("queryParam") PartnerDomainTopNChangeDetail domainTopNChangeDetail, @Param("resultTable") String resultTable);
}
