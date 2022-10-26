package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNChange;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/7
 * @DESC
 */
@Repository
public interface PartnerDomainTopNChangeMapper {

    Long countFindRankNumberByParam(@Param("queryParam") PartnerDomainTopNChange partnerDomainTopN, @Param("resultTable") String resultTable);

    List<PartnerDomainTopNChange> findRankNumberByParam(@Param("queryParam") PartnerDomainTopNChange partnerDomainTopN, @Param("resultTable") String resultTable);
}
