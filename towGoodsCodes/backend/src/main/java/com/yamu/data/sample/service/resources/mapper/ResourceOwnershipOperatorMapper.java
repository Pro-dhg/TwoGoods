package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.OperatorProportionBO;
import com.yamu.data.sample.service.resources.entity.bo.OwnershipOperatorListBO;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceData;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceOwnershipOperatorVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dys
 * @Date 2022/3/15
 */

@Repository
public interface ResourceOwnershipOperatorMapper {

    Long getDataListTotal(@Param("queryParam") ResourceOwnershipOperatorVO resourceOwnershipOperatorVO,@Param("queryTable")String queryTable);

    List<OwnershipOperatorListBO> getDataList(@Param("queryParam") ResourceOwnershipOperatorVO resourceOwnershipOperatorVO,@Param("queryTable")String queryTable);

    List<OperatorProportionBO> getOperatorProportion(@Param("queryParam") ResourceOwnershipOperatorVO resourceOwnershipOperatorVO,@Param("queryTable")String queryTable);

    List<ResourceDistributionProvinceData> resourceDistributionProvince(@Param("queryParam") ResourceOwnershipOperatorVO resourceOwnershipOperatorVO, @Param("queryTable") String queryTable);

    List<ResourceDistributionProvinceDetail> resourceDistributionProvinceDetail(@Param("queryParam") ResourceOwnershipOperatorVO resourceOwnershipOperatorVO, @Param("queryTable") String queryTable);

}
