-- 创建数据库
CREATE DATABASE IF NOT EXISTS two_goods;
CREATE DATABASE IF NOT EXISTS goods_event; --events_all 所有事件

-- 创建表
-- 缓存原始本地表
DROP TABLE IF EXISTS two_goods.dim_user_user ;
CREATE TABLE two_goods.dim_user_user_local --实名认证: 主键 姓名 昵称 生日 年龄 性别 手机号 邮箱 国家 省 市 区 邮编
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
DROP TABLE IF EXISTS two_goods.dim_user_user ;
CREATE TABLE IF NOT EXISTS two_goods.dim_user_user  AS two_goods.dim_user_user_local ENGINE = Distributed(clickhouse_cluster, two_goods, dim_user_user_local,rand());

insert into two_goods.dim_user_user_local values
    ( '001','亮亮','2022-10-27 20:16:00','拉了一泡屎'),
    ( '001','亮亮','2022-10-27 20:17:00','吃了一包辣条') ,
    ( '001','亮亮','2022-10-27 20:18:00','玩了会儿手机'),
    ( '001','亮亮','2022-10-27 20:19:00','起身去拿零食'),
    ( '001','亮亮','2022-10-27 20:20:00','吃零食') ,
    ( '002','蛋蛋','2022-10-27 20:16:00','看书'),
    ( '002','蛋蛋','2022-10-27 20:17:00','玩手机'),
    ( '002','蛋蛋','2022-10-27 20:18:00','下床走动') ,
    ( '002','蛋蛋','2022-10-27 20:20:00','干饭'),
    ( '003','小杰','2022-10-27 20:16:00','备课'),
    ( '003','小杰','2022-10-27 20:19:00','讲课') ,
    ( '004','光光','2022-10-27 20:18:00','疯狂码代码'),
    ( '005','杉杉','2022-10-27 20:17:00','工作') ;