package com.yamu.data.sample.service.resources.entity.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseEntity;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class UserDistributionBO extends BaseEntity {

    //    1分钟
    private static String RPT_1MIN = "rpt_resource_user_distribution_1min";
    //    10分钟
    private static String RPT_10MIN = "rpt_resource_user_distribution_10min";
    //    1小时
    private static String RPT_1H = "rpt_resource_user_distribution_1h";
    //    1天
    private static String RPT_1D = "rpt_resource_user_distribution_1d";
    //    1周
    private static String RPT_1W = "rpt_resource_user_distribution_1w";
    //    1月
    private static String RPT_1M = "rpt_resource_user_distribution_1m";
    //    1季度
    private static String RPT_1Q = "rpt_resource_user_distribution_1q";
    //    1年
    private static String RPT_1Y = "rpt_resource_user_distribution_1y";

    private Long rankNumber;

    private String businessIp;

    private String districtsCode;

    private String country;

    private String province;

    private String city;

    private String district;

    private String isp;

    private String userType;

    private Date parseTime;

    private String answerFirstIpDistrictsName;

    private String answerFirstIsp;

    private BigInteger parseTotalCnt;

    private BigInteger successCnt;

    private BigInteger failCnt;

    private String ispCode;

    // 辅助字段用来决定查询的表
    private String queryType;
    private String queryTable = RPT_1MIN;

    private Integer topN;

    // 辅助字段, 用于查询运营商和区域的
    private String distCode;

    // cvs导出字段
    private Double rate;
    // csv内容
    public static final String CSV_NAME = "全国-省份-访问热点域名用户明细数据";
    public static final String CSV_HEAD = "日期,省份/地区,用户量解析分布数据,占比\n";
    public  StringBuffer csvLine = new StringBuffer();

    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine.append(timeRange).append(",")
                .append(province).append(",")
                .append(parseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(rate, 2)).append("\n");
        return this.csvLine.toString();
    }


    /**
     * 如果是全国节点（ispCode="" && districtsCode=""）|| 如果是运营商节点（districtsCode=""） || 如果是省市节点（ispCode 和 districtsCode 都不为空）
     * @param distCode
     */
    public void setDistCode(String distCode) {
        if(distCode.equals("0")) { //全国节点处理
            this.ispCode = "";
            this.districtsCode = "";
        } else {
            String[] codeList = distCode.split("_");
            if(codeList.length == 1) { //运营商节点处理
                //只是查询运营商信息
                if(codeList[0].length() == 6) {
                    long code = Long.parseLong(codeList[0]);
                    this.districtsCode = String.valueOf(code / 10000);
                } else {
                    this.ispCode = codeList[0];
                }
            } else if(codeList.length == 2) {
                this.ispCode = codeList[0];
                long code = Long.parseLong(codeList[1]);
                if(code % 10000 == 0) { //省节点处理
                    this.districtsCode = String.valueOf(code / 10000);
                } else if(code % 100 == 0) { //市节点处理
                    this.districtsCode = String.valueOf(code / 100);
                } else { //区节点处理
                    this.districtsCode = String.valueOf(code);
                }
            }
        }
        this.distCode = distCode;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
        this.queryTable = "";
        switch (queryType) {
            case "1min":
                this.queryTable = RPT_1MIN;
                break;
            case "10min":
                this.queryTable = RPT_10MIN;
                break;
            case "1h":
                this.queryTable = RPT_1H;
                break;
            case "1d":
                this.queryTable = RPT_1D;
                break;
            case "1w":
                this.queryTable = RPT_1W;
                break;
            case "1m":
                this.queryTable = RPT_1M;
                break;
            case "1q":
                this.queryTable = RPT_1Q;
                break;
            case "1y":
                this.queryTable = RPT_1Y;
                break;
            default:
                this.queryTable = RPT_1MIN;
        }
    }
    public void setRankNumber(Long rankNumber) {
        if (rankNumber == null || rankNumber == 0L) {
        } else {
            this.rankNumber = rankNumber;
        }
    }

    @JsonIgnore
    private String statisticsWay;

    private String timeRange;

}
