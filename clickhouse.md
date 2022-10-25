```
#生成sha256方法
PASSWORD=$(base64 < /dev/urandom | head -c14); echo "$PASSWORD"; echo -n "$PASSWORD" | sha256sum | tr -d '-'

结果:
mHU8DkAFas3fRZ
d4f1d2baa651ff79b4f1ff869689bbd3c80b6d9211894b50256c3bdc28f5a649
```

数据库相关命令:

```shell
#登录数据库
clickhouse-client --user data_manager --password mHU8DkAFas3fRZ --host 0.0.0.0 --port 9000 
#数据库建表
clickhouse-client --user data_manager --password mHU8DkAFas3fRZ.ZwD%Q7 --host 0.0.0.0 --port 9000 --lock_acquire_timeout 36000 --multiquery <  ./src.sql

```



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

