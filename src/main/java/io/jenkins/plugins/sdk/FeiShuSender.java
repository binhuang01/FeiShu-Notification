package io.jenkins.plugins.sdk;

import io.jenkins.plugins.model.MessageModel;

import io.jenkins.plugins.sdk.FeiShuRequest.Card;
import io.jenkins.plugins.sdk.FeiShuRequest.Post;
import io.jenkins.plugins.sdk.FeiShuRequest.Text;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
public class FeiShuSender {

    /**
     * 发送 text 类型的消息
     * @param msg 消息
     *
     */
    public String sendText(MessageModel msg) {
        Text text = new FeiShuRequest.Text();
        text.setText(msg.getText());
        return call(text,msg.getWebhook(),msg.getProxy());
    }

    /**
     * 发送 post 类型的消息
     * @param msg 消息
     */
    public String sendPost(MessageModel msg) {
        Post post = new FeiShuRequest.Post();
        post.setPost(msg.getText());
        return call(post,msg.getWebhook(),msg.getProxy());
    }

    /**
     * 发送 interactive 类型的消息
     * @param msg 消息
     */
    public String sendInteractive(MessageModel msg) {
        Card card = new FeiShuRequest.Card();
        card.setCard(msg.getText());
        return call(card,msg.getWebhook(),msg.getProxy());
    }



    /**
     * 统一处理请求
     * @param request 请求
     */
    private String call(FeiShuRequest request,String webhook,String proxy) {
        try {
            Map<String, Object> content = request.getParams();
            if (proxy != null && !("").equals(proxy)) {
                content.put("webhook",webhook);
                webhook = proxy;
            }
            HttpResponse response =
                    HttpRequest.builder()
                            .server(webhook)
                            .data(content)
                            .method(Constants.METHOD_POST)
                            .build()
                            .request();
            String body = response.getBody();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
