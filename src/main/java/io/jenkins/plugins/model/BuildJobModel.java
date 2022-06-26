package io.jenkins.plugins.model;

import hudson.FilePath;
import hudson.model.Cause.UserIdCause;
import hudson.model.Executor;
import hudson.model.Run;
import io.jenkins.plugins.sdk.Constants;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BuildJobModel {

    /** Project Name **/
    private String projectName;

    /** Job ID **/
    private String jobId;

    /** Project Url **/
    private String projectUrl;

    /** Job Name **/
    private String jobName;

    /** Job Url **/
    private String jobUrl;

    /** Build Status **/
    private String statusType;

    /** Duration Time **/
    private String duration;

    /** Executor Name **/
    private String executorName;

    /** Exec User **/
    private String execUser;

    /** User Msg **/
    private String userMsg;


    public static BuildJobModel buildJobMsg(Run<?, ?> build, FilePath filePath,String text, boolean noticeAll) {
        UserIdCause cause = build.getCause(UserIdCause.class);
        String execName = null;

        String buildOn = "";
        if (build.getExecutor() != null) {
            Executor executor = build.getExecutor();
            if (executor != null) {
                buildOn = executor.getName();
            }
        }
        if (cause != null) {
            execName = cause.getUserName();
        }
        BuildJobModel jobMsgModel = BuildJobModel.builder()
                .projectName(Constants.PROJECT_NAME + build.getFullDisplayName())
                .jobId(Constants.JOB_ID + build.getId())
                .statusType(Constants.BUILD_RESULT + build.getResult())
                .duration(Constants.BUILD_DURATION + build.getDurationString())
                .executorName(Constants.BUILD_ON + buildOn)
                .execUser(Constants.EXEC_NAME + execName)
                .userMsg(text)
                .jobUrl(Constants.JENKINS_HOME + build.getUrl())
                .build();
        return jobMsgModel;
    }

}
