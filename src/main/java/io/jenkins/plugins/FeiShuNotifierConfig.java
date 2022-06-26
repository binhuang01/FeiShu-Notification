package io.jenkins.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.*;
import lombok.Data;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import java.io.IOException;


@Data
public class FeiShuNotifierConfig extends Notifier {

    /** 构建信息 **/
    private String content;

    /** proxy **/
    private String proxy;

    /** robot **/
    private String webhook;

    private boolean noticeWhenStart;
    private boolean noticeWhenFailure;
    private boolean noticeWhenSuccess;
    private boolean noticeWhenAborted;
    private boolean noticeWhenUnstable;
    private boolean noticeAll;



    /**
     * 将config.jelly参数与本类绑定
     */
    @DataBoundConstructor
    public FeiShuNotifierConfig(String webhook,
                                String content,
                                boolean noticeWhenStart,
                                boolean noticeWhenFailure,
                                boolean noticeWhenUnstable,
                                boolean noticeWhenSuccess,
                                boolean noticeWhenAborted,
                                boolean noticeAll,
                                String proxy) {
        this.webhook = webhook;
        this.content = content;
        this.proxy = proxy;
        this.noticeAll = noticeAll;
        this.noticeWhenStart = noticeWhenStart;
        this.noticeWhenFailure = noticeWhenFailure;
        this.noticeWhenSuccess = noticeWhenSuccess;
        this.noticeWhenAborted = noticeWhenAborted;
        this.noticeWhenUnstable = noticeWhenUnstable;
    }



    /**
     * 构建的执行通过这个方法定义
     * @param build    获取当前构建的信息
     * @param launcher 启动构建
     * @param listener 检查构建过程的状态
     * @return notify result
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        FeiShuSender sender = new FeiShuSender();

        //Get Final Build Result
        Result buildResult = build.getResult();
        //Get Build Result Color
        String color = buildResult != null ? buildResult.color.name() : Constants.DEFAULT_MSG_COLOR;
        //Generate  Message
        MessageModel msg = MessageModel.buildCardMessage(build, null,color,content, noticeAll,webhook, proxy);

        //Send Notify Message
        if (Result.SUCCESS.equals(buildResult) && noticeWhenSuccess) {
            sender.sendInteractive(msg);
        }
        if (Result.UNSTABLE.equals(buildResult) && noticeWhenUnstable) {
            sender.sendInteractive(msg);
        }
        if (Result.FAILURE.equals(buildResult) && noticeWhenFailure) {
            sender.sendInteractive(msg);
        }
        if (Result.ABORTED.equals(buildResult) && noticeWhenAborted) {
            sender.sendInteractive(msg);
        }


        return true;
    }


    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * 插件步骤描述
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public String getDisplayName() {
            return "FeiShu-Notification";
        }


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * webhook参数校验
         * @param value
         * @return
         */
        public FormValidation doCheckUrl(@QueryParameter("webhook") String value) {
            if (value.length() == 0) {
                return FormValidation.error("请输入一个webhook");
            }
            return FormValidation.ok();
        }

        /**
         * 检测proxy连接
         */
        public FormValidation doTestConnection(@QueryParameter("proxy") final String proxy) {
            if (proxy != null && !("").equals(proxy)) {
                try {
                    HttpRequest.builder()
                            .server(proxy)
                            .method(Constants.METHOD_POST)
                            .build()
                            .request();
                    return FormValidation.ok("Connected");
                } catch (IOException e) {
                    e.printStackTrace();
                    return FormValidation.error("Connect Fail");
                }
            } else {
                return  FormValidation.ok();
            }
        }
    }












}
