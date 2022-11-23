```
#生成sha256方法
PASSWORD=$(base64 < /dev/urandom | head -c14); echo "$PASSWORD"; echo -n "$PASSWORD" | sha256sum | tr -d '-'

结果:
mHU8DkAFas3fRZ
d4f1d2baa651ff79b4f1ff869689bbd3c80b6d9211894b50256c3bdc28f5a649
```

# 数据库相关命令

```shell
#登录数据库
clickhouse-client --user data_manager --password mHU8DkAFas3fRZ --host 0.0.0.0 --port 9000 
#数据库建表
clickhouse-client --user data_manager --password mHU8DkAFas3fRZ.ZwD%Q7 --host 0.0.0.0 --port 9000 --lock_acquire_timeout 36000 --multiquery <  ./ods.sql
#数据库信息导出
clickhouse-client -h 127.0.0.1 --port 9000 --user data_engine --password DLKUc4M.ZwD%Q7 --query=" \
select * \
from ods.ods_behavior_data \
FORMAT CSVWithNames" > ./ods_behavior_data.csv

```


# 安装配置
```
clickhouse安装 安装包 clickhouse22_rpm.tar.gz clickhouse_configs.tar.gz

# 安装
sudo rpm -Uvh *.rpm
 
# 启动服务 sudo systemctl start clickhouse-server

# 停止服务
sudo systemctl stop clickhouse-server

#修改配置文件
sudo cp -r clickhouse_configs/* /etc/clickhouse-server/ #复制配好的配置文件,放到旧的文件夹下

sudo chown -R clickhouse:clickhouse /etc/clickhouse-server/
sudo chmod -R a+rx /etc/clickhouse-server/
sudo chmod -R u+rwx /etc/clickhouse-server/
 
sudo mkdir  /data/clickhouse 
sudo chown -R clickhouse:clickhouse /data/clickhouse
 
sudo mkdir -p /var/log/clickhouse-server
sudo chown -R clickhouse:clickhouse /var/log/clickhouse-server
sudo chmod -R a+rx /var/log/clickhouse-server
sudo chmod -R u+rwx /var/log/clickhouse-server

#配置集群
sudo vi  /etc/clickhouse-server/config.xml 配置服务器集群

remote_servers>
        <clickhouse_cluster>
            <shard>
                <replica>
                    <host>clickhouse01</host>
                    <port>9000</port>
                    <user>data_manager</user>
                    <password>mHU8DkAFas3fRZ</password>
                </replica>
            </shard>
            <shard>
                <replica>
                    <host>clickhouse02</host>
                    <port>9000</port>
                    <user>data_manager</user>
                    <password>mHU8DkAFas3fRZ</password>
                </replica>
            </shard>
        </clickhouse_cluster>
</remote_servers>


#修改日志级别

    <logger>
       <!--
       所有日志级别：
             - none (turns off logging)
             - fatal
             - critical
             - error
             - warning
             - notice
             - information
             - debug
             - trace -->
       <level>warning</level>
       <log>/var/log/clickhouse-server/clickhouse-server.log</log>
       <errorlog>/var/log/clickhouse-server/clickhouse-server.err.log</errorlog>
       <size>256M</size>
       <count>40</count>
   </logger>
   
# 修改数据目录
sed -i "s/\/var\/lib\/clickhouse/\/data\/clickhouse/g" /etc/clickhouse-server/config.xml

#根据自己的集群情况添加或者注释Zookeeper节点
 <zookeeper>
    <node>
        <host>ch_zookeeper</host>
        <port>2181</port>
    </node>
</zookeeper>

#根据自己的集群修改user.xml
<!-- 单次查询所使用的最大内存 默认80G -->
<max_memory_usage>82212254720</max_memory_usage>

#最后
sudo systemctl restart clickhouse-server
sudo systemctl enable clickhouse-server #设置clickhouse开机启动
ps -ef | grep clickhouse
sudo systemctl status clickhouse-server
sudo tail -f /var/log/clickhouse-server/clickhouse-server.log
```

```shell
#下面是clickhouse同步pg的方法，pg放一些维表什么的
-- 启用物化引擎
set allow_experimental_database_materialized_postgresql=1;

-- 创建物化库
DROP DATABASE IF EXISTS dim;
CREATE DATABASE IF NOT EXISTS dim
ENGINE = MaterializedPostgreSQL('127.0.0.1:5432', '数据库', '用户名', '用户密码') ;

#具体做法可参考百度,搜MaterializedPostgreSQL，就有一大堆
```

# 下面是clickhouse的建表
```shell
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ods;

-- 创建表
-- 缓存原始本地表
DROP TABLE IF EXISTS ods.ods_behavior_data_local ;
CREATE TABLE ods.ods_behavior_data_local
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

-- 表添加字段或者删除字段 (注意:分布式表中本地表和分布式表都要更改)
ALTER TABLE 表名称 ON 集群名称 ADD COLUMN 列名称 类型

ALTER TABLE ods.ods_behavior_data_local ON cluster clickhouse_cluster ADD COLUMN uuid UInt64 COMMENT '唯一id'  ;
ALTER TABLE ods.ods_behavior_data ON cluster clickhouse_cluster ADD COLUMN uuid UInt64 COMMENT '唯一id' ;

ALTER TABLE ods.ods_behavior_data_local ON cluster clickhouse_cluster DROP COLUMN uuid  ;
ALTER TABLE ods.ods_behavior_data ON cluster clickhouse_cluster ADD DROP COLUMN uuid  ;

```

```
-- 经常使用的sql
select if(1>2,1,2) ;
select if(1>2 or 1>3 or match(lower('123'),'123') ,4,5);
select multiIf(1>2,1,2>1,8,8) ;
select case when 1>2 then 1 else 2 end ;
select case when 1>2 then 1
when 1>2 then 1
when 1>2 then 1
else 2 end ;
select concat('123','321');
select splitByChar(',','123,234,345')[1];
select cutToFirstSignificantSubdomain('0.0.0.0.0.0.0.0.0.0.0.0.2.1.0.0.5.2.0.0.0.0.b.0.4.4.c.8.9.0.4.2.ip6.arpa') ;
```
