package io.jenkins.plugins.msg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.Constants;

import java.util.ArrayList;
import java.util.List;

public class PostMsg {


    public JSONObject buildAtMsg(String openId) {
        JSONObject at = new JSONObject();
        at.put(Constants.TAG,Constants.AT);
        at.put("user_id",openId);
        at.put("user_name","所有人");
        return at;
    }


    public  JSONObject buildTextMsg(String text) {
        JSONObject textMsg = new JSONObject();
        textMsg.put(Constants.TAG,Constants.MSG_TYPE_TEXT);
        textMsg.put(Constants.MSG_TYPE_TEXT,text);
        return textMsg;
    }


    public  JSONObject buildLinkMsg(String desc,String url) {
        JSONObject link = new JSONObject();
        link.put(Constants.TAG,Constants.A_LABEL);
        link.put(Constants.MSG_TYPE_TEXT,desc);
        link.put(Constants.LINK,url);
        return link;
    }



    private  List<String> buildJobMsgBody(BuildJobModel jobModel) {
        List<String> jobMsg = new ArrayList<>();
        jobMsg.add(jobModel.getProjectName().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        jobMsg.add(jobModel.getJobId().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        jobMsg.add(jobModel.getStatusType().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        jobMsg.add(jobModel.getDuration().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        jobMsg.add(jobModel.getExecutorName().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        jobMsg.add(jobModel.getExecUser().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        jobMsg.add(jobModel.getJobUrl().replaceAll("\\**","")  + Constants.LINE_SEPARATOR);
        return jobMsg;
    }


    public  JSONObject buildPostMsgContent(BuildJobModel jobModel,boolean noticeAll) {
        JSONObject postMsgContent = new JSONObject();
        JSONObject cn_msg = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONArray content;

        List<String> jobMsg = buildJobMsgBody(jobModel);
        for (int i = 0; i < jobMsg.size(); i++) {
            content = new JSONArray();
            String text = jobMsg.get(i);
            JSONObject msg;
            if (text.contains("http://")) {
                msg = buildLinkMsg("项目地址",text);
            } else {
                msg = buildTextMsg(text);
            }
            content.add(msg);
            contents.add(content);
        }
        if (noticeAll) {
            content = new JSONArray();
            content.add(buildAtMsg("all"));
            contents.add(content);
        }
        cn_msg.put(Constants.TITLE, MessageModel.DEFAULT_TITLE);
        cn_msg.put(Constants.MSG_CONTENT,contents);
        postMsgContent.put(Constants.DEFAULT_LANG,cn_msg);
        return postMsgContent;
    }


    public  JSONObject buildPostMsg(BuildJobModel jobMsg, boolean noticeAll) {
        JSONObject postMsg = new JSONObject();
        JSONObject postMsgContent = buildPostMsgContent(jobMsg,noticeAll);
        postMsg.put(Constants.MSG_TYPE_POST,postMsgContent);
        return postMsg;
    }
}
