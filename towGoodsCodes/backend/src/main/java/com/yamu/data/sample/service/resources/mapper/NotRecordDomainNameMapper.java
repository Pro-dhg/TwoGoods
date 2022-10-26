package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.NotRecordDomainNameList;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameDetailList;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dys
 * @Date 2022/4/14
 */
@Repository
public interface NotRecordDomainNameMapper {

    Long getTotal(@Param("queryParam") RecordDomainNameParam recordDomainNameParam, @Param("queryTable")String queryTable);

    List<NotRecordDomainNameList> getList(@Param("queryParam") RecordDomainNameParam recordDomainNameParam, @Param("queryTable")String queryTable);

    NotRecordDomainNameList getSumCnt(@Param("queryParam") RecordDomainNameParam recordDomainNameParam, @Param("queryTable")String queryTable);

    List<RecordDomainNameDetailList> getDetailList(@Param("queryParam") RecordDomainNameParam recordDomainNameParam, @Param("queryTable")String queryTable);
}
