# python-3.10.5 安装

```shell
 安装包: python.3.10.5.tar.gz 
 tar -zxvf tar -zxvf Python-3.10.5.tgz
 
 cd python
 1.安装环境依赖
 rpm -ivh ./rpm/*.rpm --force --nodeps

 2.安装python
 cd Python-3.10.5
 ./configure --prefix=/usr/local/python3
 make && make install

 3.查看python安装结果
 /usr/local/python3/bin/python3 -V
 ### Python 3.10.5
 /usr/local/python3/bin/pip3 -V
 ### pip 22.0.4 from /usr/local/python3/lib/python3.10/site-packages/pip (python 3.10)

 4.配置软链
 rm -f /usr/bin/python3
 rm -f /usr/bin/pip3
 ln -s /usr/local/python3/bin/python3 /usr/bin/python3
 ln -s /usr/local/python3/bin/pip3 /usr/bin/pip3
```