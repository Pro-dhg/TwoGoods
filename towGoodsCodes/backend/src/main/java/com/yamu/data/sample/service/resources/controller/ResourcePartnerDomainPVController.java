package com.yamu.data.sample.service.resources.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;

import com.yamu.data.sample.service.resources.entity.po.PartnerDomainPV;
import com.yamu.data.sample.service.resources.service.ResourcePartnerDomainPVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
/**
 * @author getiejun
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/resourcePartnerDomainPV")
public class ResourcePartnerDomainPVController {

    @Autowired
    private ResourcePartnerDomainPVService domainPVService;

    @GetMapping("/v1")
    public ResponseEntity findByPage(PartnerDomainPV partnerDomainPV) {
        PageResult pageResult = domainPVService.findByPage(partnerDomainPV);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 导出文件.
     *
     * @return
     */
    @GetMapping("/download/v1")
    public void download(HttpServletResponse response, PartnerDomainPV partnerDomainPV) throws IOException {
//        checkDownloadMethodParam(partnerDomainPV);
        Map<String, List<JSONObject>> result = domainPVService.downloadBySelective(partnerDomainPV);
        String timeInterval = partnerDomainPV.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + partnerDomainPV.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "特定域名访问量报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();
        //  循环resultMap,根据类型创建excel 分类
        int index = 0;
        for (Map.Entry<String, List<JSONObject>> entrySet : result.entrySet()) {
            index++;
            String key = entrySet.getKey();
            List<JSONObject> sheetDateList = entrySet.getValue();
            writer.clearHeaderAlias();

            renameOrSetSheetName(index, key, writer);
            writer.renameSheet(key);
            writer.addHeaderAlias("序号", "序号");
            writer.addHeaderAlias("网站名称", "网站名称");
            writer.addHeaderAlias("主体(单位/企业名称)", "主体(单位/企业名称)");
            writer.addHeaderAlias("域名", "域名");
            writer.addHeaderAlias("访问量", "访问量");
            setColumnWidth(writer, 4, 25);
            // 设置数据
            writer.write(sheetDateList, true);
        }
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

//    private void checkDownloadMethodParam(PartnerDomainPV partnerDomainPV) throws UnsupportedEncodingException {
//        if(ObjectUtil.isNotEmpty(partnerDomainPV.getDomainType())) {
//            partnerDomainPV.setDomainType(URLDecoder.decode(partnerDomainPV.getDomainType(), "utf-8"));
//        }
//    }

    private void setColumnWidth(ExcelWriter writer, int columnNumber, int width) {
        for(int index = 0; index < columnNumber; index++) {
            writer.setColumnWidth(index, width);
        }
    }

    private void renameOrSetSheetName(int index, String sheetName, ExcelWriter writer) {
        if(index == 1) {
            writer.renameSheet(sheetName);
        } else {
            writer.setSheet(sheetName);
        }
    }

}
