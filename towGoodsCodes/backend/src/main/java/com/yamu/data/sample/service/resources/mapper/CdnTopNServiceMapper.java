package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.CdnTopNServiceCompanyParam;
import com.yamu.data.sample.service.resources.entity.bo.CdnTopNServiceParam;
import com.yamu.data.sample.service.resources.entity.po.TopNServiceData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dys
 * @date 2022/09/22
 */
@Repository
public interface CdnTopNServiceMapper {

    Long cdnTopNServiceCompanyTotal(@Param("queryParam") CdnTopNServiceParam cdnTopNServiceParam,@Param("queryTable")String queryTable);

    List<TopNServiceData> cdnTopNServiceCompanyList(@Param("queryParam") CdnTopNServiceParam cdnTopNServiceParam, @Param("queryTable")String queryTable);

    TopNServiceData cdnTopNServiceCompanySumData(@Param("queryParam") CdnTopNServiceParam cdnTopNServiceParam, @Param("queryTable")String queryTable);

    Long cdnTopNServiceDomainTotal(@Param("queryParam") CdnTopNServiceParam cdnTopNServiceParam,@Param("queryTable")String queryTable);

    List<TopNServiceData> cdnTopNServiceDomainList(@Param("queryParam") CdnTopNServiceParam cdnTopNServiceParam, @Param("queryTable")String queryTable);

    TopNServiceData cdnTopNServiceDomainSumData(@Param("queryParam") CdnTopNServiceParam cdnTopNServiceParam, @Param("queryTable")String queryTable);

    Long cdnBusinessTopNServiceCompanyTotal(@Param("queryParam") CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam, @Param("queryTable")String queryTable);

    List<TopNServiceData> cdnBusinessTopNServiceCompanyList(@Param("queryParam") CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam, @Param("queryTable")String queryTable);

    TopNServiceData cdnBusinessTopNServiceCompanySumData(@Param("queryParam") CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam, @Param("queryTable")String queryTable);

    Long cdnusinessTopNServiceDomainTotal(@Param("queryParam") CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam,@Param("queryTable")String queryTable);

    List<TopNServiceData> cdnusinessTopNServiceDomainList(@Param("queryParam") CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam, @Param("queryTable")String queryTable);

    TopNServiceData cdnusinessTopNServiceDomainSumData(@Param("queryParam") CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam, @Param("queryTable")String queryTable);
}
