package io.jenkins.plugins;


import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.Constants;
import io.jenkins.plugins.sdk.FeiShuSender;
import io.jenkins.plugins.sdk.HttpRequest;
import jenkins.tasks.SimpleBuildStep;
import lombok.Data;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;

@Data
public class FeiShuPipeline extends Builder implements SimpleBuildStep {


    private String webhook;
    private String msg;
    private String type;
    private final boolean atAll;
    private String proxy;

    @DataBoundConstructor
    public FeiShuPipeline(@Nonnull String webhook,
                          String msg,
                          String type,
                          boolean atAll,
                          String proxy) {
        this.webhook = webhook;
        this.msg     = msg;
        this.type    = type;
        this.atAll   = atAll;
        this.proxy   = proxy;
    }


    /**
     * 插件处理逻辑
     * @param build
     * @param filePath
     * @param launcher
     * @param taskListener
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public void perform(@Nonnull Run<?, ?> build,
                        @Nonnull FilePath filePath,
                        @NonNull EnvVars env,
                        @Nonnull Launcher launcher,
                        @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        taskListener.getLogger().println("构建结果通知");
        FeiShuSender sender = new FeiShuSender();

        Result result = build.getResult();

        String color = result == null ? "grep" : result.color.name();

        MessageModel msg;

        if (MsgTypeEnum.POST.name().equals(type)) {
            msg = MessageModel.buildPostMessage(build,filePath,color,getMsg(),atAll, webhook, proxy);
            sender.sendPost(msg);
        } else {
            msg = MessageModel.buildCardMessage(build,filePath,color,getMsg(),atAll, webhook, proxy);
            sender.sendInteractive(msg);
        }

    }



    @Symbol("FeiShu")
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "send build message to feishu";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
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
