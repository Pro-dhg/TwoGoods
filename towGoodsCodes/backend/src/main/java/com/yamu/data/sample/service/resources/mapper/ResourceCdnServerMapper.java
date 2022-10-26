package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dys
 * @Date 2022/06/24
 */
@Repository
public interface ResourceCdnServerMapper {

    Long getTotal(@Param("queryParam") ResourceCdnServerParam resourceCdnServerParam, @Param("queryTable")String queryTable);

    List<ResourceCdnServerList> getList(@Param("queryParam") ResourceCdnServerParam resourceCdnServerParam, @Param("queryTable")String queryTable);

    List<ResourceCdnServerTrend> getTrend(@Param("queryParam") ResourceCdnServerParam resourceCdnServerParam, @Param("queryTable")String queryTable);

    List<ResourceCdnServerDispatchTrend> getDispatchTrend(@Param("queryParam") ResourceCdnServerParam resourceCdnServerParam, @Param("queryTable")String queryTable);

    Long getDetailListTotal(@Param("queryParam") ResourceCdnServerParam resourceCdnServerParam, @Param("queryTable")String queryTable);

    List<ResourceCdnServerDetailList> getDetailList(@Param("queryParam") ResourceCdnServerParam resourceCdnServerParam, @Param("queryTable")String queryTable);
}
