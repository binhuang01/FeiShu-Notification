package io.jenkins.plugins.config;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.Secret;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FeiShuRobotConfig implements Describable<FeiShuRobotConfig> {


    private String name;

    private Secret webhook;

    public FeiShuRobotConfig(String name,String webhook) {
        this.name = name;
        this.webhook = Secret.fromString(webhook);
    }


    public String getWebhook() {
        if (webhook == null) {
            return null;
        }
        return webhook.getPlainText();

    }

    @Override
    public Descriptor<FeiShuRobotConfig> getDescriptor() {
        return null;
    }
}
