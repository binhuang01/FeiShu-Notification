package io.jenkins.plugins.model;

import com.alibaba.fastjson.JSONObject;
import hudson.FilePath;
import hudson.model.AbstractBuild;

import hudson.model.Run;
import io.jenkins.plugins.enums.MsgTypeEnum;

import io.jenkins.plugins.msg.CardMsg;
import io.jenkins.plugins.msg.PostMsg;
import lombok.Builder;
import lombok.Data;



@Builder
@Data
public class MessageModel {

    public static final String DEFAULT_TITLE = "Jenkins 构建通知";

    /** 消息类型 **/
    private MsgTypeEnum type;

    /** 标题 **/
    private String title;

    /** webhook **/
    private String webhook;

    /** 消息正文 **/
    private String text;

    /** proxy **/
    private String proxy;



    public static MessageModel buildPostMessage(Run<?, ?> build, FilePath filePath,String buildResult, String content, boolean noticeAll, String webhook, String proxy) {

        BuildJobModel jobMsg = BuildJobModel.buildJobMsg(build,filePath,content,noticeAll);
        PostMsg post = new PostMsg();
        JSONObject postMsg = post.buildPostMsg(jobMsg,noticeAll);
        MessageModel msg = MessageModel.builder()
                .text(postMsg.toJSONString())
                .webhook(webhook)
                .proxy(proxy)
                .type(MsgTypeEnum.POST)
                .title(DEFAULT_TITLE)
                .build();
        return msg;
    }


    public static MessageModel buildCardMessage(Run<?, ?> run,FilePath filePath,String buildResult,String content,boolean noticeAll,String webhook,String proxy) {
        if (run != null) {
            BuildJobModel jobMsg = BuildJobModel.buildJobMsg(run, null, content, noticeAll);
            CardMsg card = new CardMsg();
            String cardMsg = card.buildCardMsg(jobMsg, buildResult, noticeAll);
            MessageModel msg = MessageModel.builder()
                    .text(cardMsg)
                    .webhook(webhook)
                    .proxy(proxy)
                    .type(MsgTypeEnum.INTERACTIVE)
                    .title(DEFAULT_TITLE)
                    .build();
            return msg;
        }
        return  null;
    }


}
