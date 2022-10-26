package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationDetailSelect;
import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationMap;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zhang Yanping
 * @Date 2021/8/26
 * @DESC
 */

@Repository
public interface DomainCNameRelationMapMapper {

    Long countFind(@Param("param") DomainCNameRelationMap crm);

    List<DomainCNameRelationMap> find(@Param("param") DomainCNameRelationMap crm);

    List<DomainCNameRelationMap> download(@Param("param") DomainCNameRelationMap crm);

    Long countFindDetail(@Param("param") DomainCNameRelationDetailSelect crm);

    List<DomainCNameRelationDetailSelect> findDetail(@Param("param") DomainCNameRelationDetailSelect crm);

}
