package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopN;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ResourceWebsiteTopNMapper {

    List<ResourceWebsiteTopN> findWebsiteTopNByParam(@Param("queryParam") ResourceWebsiteTopN resourceWebsiteTopN);
    List<ResourceWebsiteTopN> findLastWebsiteTopNByParam(@Param("queryParam") ResourceWebsiteTopN resourceWebsiteTopN);
}
