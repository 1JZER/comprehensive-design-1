# comprehensive-design
ZUEL comprehensive-design


## MySQL开启binlog

在wsl用户目录下

```
mkdir mysql5.7
cd  mysql5.7
mkdir conf
cd conf
vim my.conf
```

粘贴以下信息

```
[mysqld] 
log-bin=mysql-bin # 开启 binlog 
binlog-format=ROW # 选择 ROW 模式 
server_id=1
```



记得把xxxxx更换为自己的用户目录

```
docker run --name mysql \
-p 3306:3306 \
-e MYSQL_ROOT_HOST='%' \
-e MYSQL_ROOT_PASSWORD=root \
-v /home/xxxxxx/mysql5.7/conf:/etc/mysql/conf.d \
-d mysql:5.7.36
```





idea连接mysql执行

```
CREATE USER canal IDENTIFIED BY 'canal';  
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
FLUSH PRIVILEGES;
```



完成


