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



