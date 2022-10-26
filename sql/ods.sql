-- 创建数据库
CREATE DATABASE IF NOT EXISTS two_goods;
CREATE DATABASE IF NOT EXISTS dawlous; --events_all 所有事件

-- 创建表
-- 缓存原始本地表
DROP TABLE IF EXISTS dim.dim_user_user ;
CREATE TABLE dim.dim_user_use --实名认证: 主键 姓名 昵称 生日 年龄 性别 手机号 邮箱 国家 省 市 区 邮编
(
    `id` String  COMMENT 'id',
    `name` String  COMMENT '姓名',
    `parse_timestamp` DateTime  COMMENT '解析时间',
    `behavior` String  COMMENT '行为'
)
ENGINE = MergeTree
PARTITION BY formatDateTime(parse_timestamp, '%Y%m%d%H')
ORDER BY (parse_timestamp, id, name)
TTL parse_timestamp + toIntervalDay(14)
SETTINGS index_granularity = 8192;


-- 建分布式表
DROP TABLE IF EXISTS ods.ods_behavior_data ;
CREATE TABLE IF NOT EXISTS ods.ods_behavior_data  AS ods.ods_behavior_data_local ENGINE = Distributed(clickhouse_cluster, ods, ods_behavior_data_local,rand());