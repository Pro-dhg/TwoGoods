package com.yamu.data.sample.service.resources.common.utils.csv;


import com.yamu.data.sample.service.common.util.DateUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class ExportCsvFileUtil {
    public static final String ENCODE_GBK = "GBK";

    public static void exportCsv(String csvName, String startTime, String endTime, String csvHead, List<String> csvLines, HttpServletResponse response) throws Exception {
        String l = DateUtils.formatDataToString(new Date(),"yyyyMMddHHmmss");
        String fileName = csvName + "_" + l + ".csv";
        String remarkText = new StringBuffer().append("备注：导出时间").append(",").append(startTime).append(",").
                append(endTime).append("\n").toString();
        response.setContentType("application/ms-txt.numberformat:@");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(remarkText.getBytes(ENCODE_GBK));
            out.write(csvHead.getBytes(ENCODE_GBK));
            for (String buffer : csvLines) {
                out.write(buffer.getBytes(ENCODE_GBK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}
