package io.jenkins.plugins.msg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.Constants;

public class CardMsg {

    /** markdown消息相关 **/
    private static final String CARD_DIV    = "div";
    private static final String MARKDOWN    = "lark_md";
    private static final String PLAINT_TEXT = "plain_text";

    /** button相关 **/
    private static final String ELEMENT_BUTTON_TYPE = "button";
    private static final String BTN_ACTION  = "action";
    private static final String BTN_CONTENT = "actions";
    private static final String BTN_PROJECT_NAME    = "项目地址";
    private static final String BTN_TYPE    = "type";
    private static final String BTN_PRIMARY_TYPE = "primary";
    private static final String BTN_URL = "url";

    /** note相关 **/
    private static final String ELEMENT_NOTE_TYPE = "note";

    /** 图片相关 **/
    private static final String ELEMENT_IMG_TYPE = "img";
    private static final String IMG_KEY = "img_key";
    private static final String IMG_TIP = "alt";

    /** 卡片消息相关 **/
    private static final String CARD_HEAD    = "header";
    private static final String CARD_MODULES = "elements";
    private static final String ELEMENT_SEPARATOR = "hr";
    private static final String AT_ALL = "<at id=all></at>";



    private JSONObject buildSeparator() {
        JSONObject separator = new JSONObject();
        separator.put(Constants.TAG,ELEMENT_SEPARATOR);
        return separator;
    }

    private JSONArray buildNoteMsg(String msg) {
        JSONArray noteMessages = new JSONArray();
        noteMessages.add(buildImageMsg("img_e61db329-2469-4da7-8f13-2d2f284c3b1g","图片"));
        noteMessages.add(buildBasicMsg(msg));
        return noteMessages;
    }


    public JSONObject buildImageMsg(String imageKey,String imageName) {
        JSONObject imageMsg = new JSONObject();
        imageMsg.put(Constants.TAG,ELEMENT_IMG_TYPE);
        imageMsg.put(IMG_KEY,imageKey);
        imageMsg.put(IMG_TIP,buildBasicMsg("Note"));
        return imageMsg;
    }


    /**
     * 构建按钮内容
     * @param btnName (按钮名称)
     * @param url     (按钮点击链接)
     * @return  按钮消息字符串
     */
    private JSONObject buildButton(String btnName,String url) {
        JSONObject btn = new JSONObject();
        JSONObject btnContent = new JSONObject();
        btn.put(Constants.TAG,ELEMENT_BUTTON_TYPE);
        btnContent.put(Constants.TAG,PLAINT_TEXT);
        btnContent.put(Constants.MSG_CONTENT,btnName);
        btn.put(Constants.TEXT,btnContent);
        btn.put(BTN_TYPE,BTN_PRIMARY_TYPE);
        btn.put(BTN_URL,url);
        return btn;
    }


    /**
     * 生成普通文本格式消息
     * @param msg (消息)
     * @return 消息字符串
     */
    private JSONObject buildBasicMsg(String msg) {
        JSONObject basicMsg = new JSONObject();
        basicMsg.put(Constants.TAG,PLAINT_TEXT);
        basicMsg.put(Constants.MSG_CONTENT,msg);
        return  basicMsg;
    }


    /**
     * 消息头
     * @param color (构建结果颜色)
     * @return 消息头部
     */
    private JSONObject buildCardMsgHeader(String color) {
        JSONObject cardHeader = new JSONObject();
        cardHeader.put(Constants.TITLE,buildBasicMsg(MessageModel.DEFAULT_TITLE));
        cardHeader.put(Constants.TEMPLATE,color);
        return cardHeader;
    }


    /**
     * 生成md格式消息
     * @param msg (消息)
     * @return md格式消息
     */
    private JSONObject buildMarkDownMsg(String msg) {
        JSONObject basicMsg = new JSONObject();
        basicMsg.put(Constants.TAG,MARKDOWN);
        basicMsg.put(Constants.MSG_CONTENT,msg);
        return  basicMsg;
    }

    /**
     * 构造markdown消息体内容
     * @param jobMsg (job消息)
     * @return job消息
     */
    private String buildCardMsgBody(BuildJobModel jobMsg,boolean atAll) {
        StringBuilder jobMsgBody = new StringBuilder();
        jobMsgBody.append(jobMsg.getProjectName() +  Constants.LINE_SEPARATOR +
                          jobMsg.getJobId()       +  Constants.LINE_SEPARATOR +
                          jobMsg.getStatusType()  +  Constants.LINE_SEPARATOR +
                          jobMsg.getDuration()    +  Constants.LINE_SEPARATOR +
                          jobMsg.getExecutorName() + Constants.LINE_SEPARATOR +
                          jobMsg.getExecUser()     + Constants.LINE_SEPARATOR );
        if (jobMsg.getUserMsg() != null && !("").equals(jobMsg.getUserMsg())) {
            jobMsgBody.append( Constants.NOTICE_MSG + jobMsg.getUserMsg() +  Constants.LINE_SEPARATOR);
        }
        if (atAll) {
            jobMsgBody.append(AT_ALL + Constants.LINE_SEPARATOR);
        }
        return  jobMsgBody.toString();
    }


    /**
     * 消息模块
     * @param jobMsg (job消息体)
     * @param atAll  (是否通知所有)
     * @return 所有elements元素
     */
    private JSONArray buildCardMsgElements(BuildJobModel jobMsg, boolean atAll) {
        JSONArray msgModules = new JSONArray();
        //msg element
        JSONObject markDownElement = new JSONObject();
        markDownElement.put(Constants.TAG,CARD_DIV);
        markDownElement.put(Constants.MSG_TYPE_TEXT,buildMarkDownMsg(buildCardMsgBody(jobMsg,atAll)));
        msgModules.add(markDownElement);

        //button element
        JSONObject btnElement = new JSONObject();
        btnElement.put(Constants.TAG,BTN_ACTION);
        JSONArray btnArr = new JSONArray();
        btnArr.add(buildButton(BTN_PROJECT_NAME,jobMsg.getJobUrl()));
        btnElement.put(BTN_CONTENT,btnArr);
        msgModules.add(btnElement);
        msgModules.add(buildSeparator());

        //note element
        JSONObject noteElement = new JSONObject();
        JSONArray noteMessages = buildNoteMsg(Constants.DEFAULT_NOTE + Constants.JENKINS_VER);
        noteElement.put(CARD_MODULES,noteMessages);
        noteElement.put(Constants.TAG,ELEMENT_NOTE_TYPE);
        msgModules.add(noteElement);

        return msgModules;
    }


    /**
     * 卡片消息主体
     * header  (头部)
     * modules (主体)
     * note    (备注)
     * @param jobMsg      (job相关消息)
     * @param buildResult (构建结果颜色)
     * @param atAll       (是否通知所有)
     * @return 卡片消息请求体
     */
    private String buildCardMsgContent(BuildJobModel jobMsg,String buildResult, boolean atAll) {
        JSONObject cardContent = new JSONObject();
        cardContent.put(CARD_HEAD,buildCardMsgHeader(buildResult));
        cardContent.put(CARD_MODULES,buildCardMsgElements(jobMsg,atAll));
        return cardContent.toJSONString();
    }


    /**
     * 构造卡片消息
     * @param jobMsg (job 消息)
     * @param atAll (是否通知所有人)
     * @return card类型消息
     */
    public String buildCardMsg(BuildJobModel jobMsg, String buildResultColor, boolean atAll) {
        return buildCardMsgContent(jobMsg,buildResultColor,atAll);
    }
}
