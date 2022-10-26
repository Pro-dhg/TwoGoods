package com.yamu.data.sample.service.resources.scheduled;

import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeKeyBusinessBO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeDataVO;
import com.yamu.data.sample.service.resources.service.ResourceVisualizationHomeService;
import com.yamu.data.sample.service.resources.visualizationHomeMap.VisualizationHomeRamMap;
import com.yamu.data.sample.service.resources.visualizationHomeMap.VisualizationHomeSchedulerMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
public class ResourceVisualizationHomeScheduler {

    @Autowired
    private VisualizationHomeSchedulerMap visualizationHomeSchedulerMap;

    @Autowired
    private VisualizationHomeRamMap visualizationHomeRamMap;

    @Autowired
    private ResourceVisualizationHomeService resourceVisualizationHomeService;

    @Value("${config.execution_date_offset}")
    public Integer executionDateOffset;

    @Scheduled(cron = "0 */1 * * * ?")
    public void testTasks() {
        String startTime = DateUtils.formatDataToString(new Date(),DateUtils.DEFAULT_DAY_FMT) + " 00:00:00";
        String endTime = DateUtils.getLaterTimeByMin(new Date(),executionDateOffset,"yyyy-MM-dd HH:mm") + ":00";
        Map<String, List> map = visualizationHomeSchedulerMap.getAllRegionMap();
        Map<String, List> paramMap = visualizationHomeRamMap.getAllRegionMap();
        VisualizationHomeDataVO visualizationHomeDataVO = new VisualizationHomeDataVO();
        visualizationHomeDataVO.setStartTime(startTime);
        visualizationHomeDataVO.setEndTime(endTime);
        visualizationHomeDataVO.setIsToday(true);
        if(paramMap.get("runkNumber") != null ){
            visualizationHomeDataVO.setRankNumber(Long.valueOf((Long) paramMap.get("runkNumber").get(0)));
        }
        if(paramMap.get("userType") != null){
            visualizationHomeDataVO.setUserType(String.valueOf((String) paramMap.get("userType").get(0)));
        }
        List<VisualizationHomeKeyBusinessBO> list = resourceVisualizationHomeService.keyBusiness(visualizationHomeDataVO);
        String mapKey = endTime+"-keyBusinessScheduler";
        if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
            mapKey += "-" + visualizationHomeDataVO.getRankNumber();
        }
        if(visualizationHomeDataVO.getUserType() != null && !"".equals(visualizationHomeDataVO.getUserType())){
            mapKey += "-" + visualizationHomeDataVO.getUserType();
        }
        visualizationHomeSchedulerMap.removeAll();
        visualizationHomeSchedulerMap.initRegion(mapKey,list);
    }

}
