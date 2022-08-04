# FeiShu-Notification
Jenkins 构建消息-飞书通知插件

支持FreeStyle 和 Pipeline风格工程
## Usage

### FreeStyle风格工程用法

![img](https://user-images.githubusercontent.com/53971532/177046892-84c0460f-35e9-4cab-b5a7-1a1c4f30ff5f.png)


webhook: 填写飞书群聊机器人的webhook

添加机器人参考: https://www.feishu.cn/hc/zh-CN/articles/360024984973

![img_1](https://user-images.githubusercontent.com/53971532/177046915-f05beb03-4c7f-4508-8915-7e5b95281cb4.png)

Message: 填写自定义通知消息

NoticeOccassions:  通知时机，支持构建成功/失败/不稳定 时通知构建结果

Proxy: 用于内网环境，通过代理将构建结果请求转发到飞书群聊。

### Pipeline用法 

通知时机通过pipeline内置语法实现，如

声明式Pipeline
```
post {
    success {
        FeiShu(webhook:'',proxy:'',message:'',atAll:false)        
    }
    failure {
        FeiShu(webhook:'',proxy:'',message:'',atAll:false)
    }
    abort {
        FeiShu(webhook:'',proxy:'',message:'',atAll:false)
    }
}
```

脚本式Pipeline
```
catchError(message:'xxx',buildResult:'failure',stageResult:'unstable') {
    FeiShu(webhook:'',proxy:'',message:'',atAll:false)
}
```

## 效果

![img_2](https://user-images.githubusercontent.com/53971532/177046910-362054c5-ef15-4639-ae7f-42f13b5f4808.png)

## Reference
部分实现参考ding talk消息通知插件
https://github.com/jenkinsci/dingtalk-plugin

## 快速开始

### 使用maven容器编译

```shell

# 下载代码
git clone https://github.com/binhuang01/FeiShu-Notification.git 

# 启动maven容器
docker run -it -d --name maven-build maven:3.8.6-jdk-11

# 复制代码到容器内
docker cp FeiShu-Notification docker maven-build:/tmp

# 进入容器
docker exec -it maven-build /bin/bash

# 进入代码文件夹
cd /tmp/FeiShu-Notification

# 下载依赖开始编译，具体时间要看网络状况，网络状况不好的话可以换一下maven源
nohup mvn install && mvn hpi:hpi -e &

# 查看编译日志（ctrl+c 退出）
tail -f nohup.out 

[INFO] Installing /tmp/FeiShu-Notification-master/FeiShu-Notification-master/target/FeiShu-Plugin.hpi to /root/.m2/repository/io/jenkins/plugins/FeiShu-Plugin/1.0-SNAPSHOT/FeiShu-Plugin-1.0-SNAPSHOT.hpi
[INFO] Installing /tmp/FeiShu-Notification-master/FeiShu-Notification-master/pom.xml to /root/.m2/repository/io/jenkins/plugins/FeiShu-Plugin/1.0-SNAPSHOT/FeiShu-Plugin-1.0-SNAPSHOT.pom
[INFO] Installing /tmp/FeiShu-Notification-master/FeiShu-Notification-master/target/FeiShu-Plugin.jar to /root/.m2/repository/io/jenkins/plugins/FeiShu-Plugin/1.0-SNAPSHOT/FeiShu-Plugin-1.0-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  58:25 min
[INFO] Finished at: 2022-08-03T11:21:32Z
[INFO] ------------------------------------------------------------------------

# 编译完成后退出容器
exit

# 复制hpi文件到宿主机
docker cp /tmp/FeiShu-Notification-master/FeiShu-Notification-master/target/FeiShu-Plugin.hpi .

# 测试hpi文件正常后删除容器
docker rm -f maven-build

```

