package io.jenkins.plugins.sdk;

import jenkins.model.Jenkins;


public class Constants {

    /** 消息类型 **/
    public static final String MSG_TYPE_TEXT = "text";

    public static final String MSG_TYPE_POST = "post";

    public static final String MSG_TYPE_CARD = "interactive";

    public static final String MSG_CONTENT = "content";

    /** UTF-8字符集 **/
    public static final String CHARSET_UTF8 = "UTF-8";

    /** HTTP请求方法 **/
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";

    /** H
     *
     * TTP请求相关 **/
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    /** 响应编码 **/
    public static final String CONTENT_ENCODING_GZIP = "gzip";

    /** 换行符 **/
    public static final String LINE_SEPARATOR = "\n";

    /**字体加粗*/
    public static final String MARK = "**";
    /** 构建节点 **/
    public static final String BUILD_ON = "**执行机：    **";
    /** 构建ID **/
    public static final String JOB_ID = "**构建号：    **";
    /** 构建结果 **/
    public static final String BUILD_RESULT = "**构建结果： **";
    /** 构建用时 **/
    public static final String BUILD_DURATION = "**构建用时： **";
    /** 执行者 **/
    public static final String EXEC_NAME = "**执行者：    **";
    /** 工程名 **/
    public static final String PROJECT_NAME = "**工程名称： **";
    /** Jenkins Version **/
    public static final String JENKINS_VER = "JENKINS VERSION: " + Jenkins.VERSION;

    public static final String NOTICE_MSG = "**备注：       **";
    /** 构建变更 **/
    public static final String BUILD_CHANGE = "构建变更： ";
    /** JENKINS HOME **/
    public static final String JENKINS_HOME = Jenkins.get().getRootUrl();
    /** 默认消息 **/
    public static final String DEFAULT_NOTE = "以上消息由Jenkins机器人自动推送                            ";


    /** Message Common **/
    public static final String TAG  = "tag";
    public static final String TEXT = "text";
    public static final String LINK = "href";
    public static final String TITLE = "title";
    public static final String A_LABEL = "a";
    public static final String TEMPLATE = "template";
    public static final String AT = "at";
    public static final String DEFAULT_LANG = "zh_cn";
    public static final String DEFAULT_MSG_COLOR = "grep";



}
