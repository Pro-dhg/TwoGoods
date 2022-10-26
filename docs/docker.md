# docker-ce-20.10.7 安装

```shell
#下载阿里源repo文件
 curl -o /etc/yum.repos.d/Centos-7.repo http://mirrors.aliyun.com/repo/Centos-7.repo
 curl -o /etc/yum.repos.d/docker-ce.repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
#检查yum源
 yum makecache
#安装docker
 yum -y install  docker-ce-20.10.7
#docker迁移数据目录
 sudo systemctl stop docker 
 sudo mv /var/lib/docker /data/docker
 sudo ln -s /data/docker /var/lib/docker
 sudo systemctl start docker
```

# docker基本命令使用
```shell
 docker pull ubuntu  #获取镜像，载入到本地
 docker save 镜像id > xxx.tar #保存镜像
 docker load --input=xxx.tar # 导入镜像
 docker tag 旧镜像名  新镜像名 #镜像改名
 docker images #查看本地镜像
 docker rmi 镜像id #删除镜像
 
 docker run -it ubuntu /bin/bash  #启动交互式容器
 docker run --name test --privileged -v /data1/:/data/ -p 12332:12332/udp -p 2332:3223/tcp -itd centos7:latest bash #启动一个镜像，名字叫test，root启动，数据卷映射/data1映射到容器内的/data下，端口映射，镜像使用为centos7：latest
 docker ps  -a #查看所有的容器，可以看到容器ID
 docker ps #查看正在运行的容器
 docker exec -it (容器名或id) /bin/bash  #进入容器（推荐使用，退出终端不会导致容器停止）

 docker export 容器id > xxx.tar #保存容器
 docker import xxx.tar container-name #导入容器
 docker run -it container-name bash #启动这个容器
 docker stop 容器ID   #停止容器
 docker restart 容器ID  #重启容器
 docker rm 容器id #删除容器
 
 docker inspect 容器id #查看容器的详细信息
 docker inspect -f '{{.ID}}' 容器名称   #查看镜像的长的ID
 docker update --restart=always 容器名称    #开机自动启动容器
```