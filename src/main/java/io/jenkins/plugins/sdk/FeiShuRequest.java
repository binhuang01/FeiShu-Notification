package io.jenkins.plugins.sdk;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public abstract class FeiShuRequest {

    /**
     * 消息类型
     * @return type
     */
    public abstract String getMsgType();

    public Map<String, Object> getParams() {
        String msgType = this.getMsgType();
        Map<String, Object> params = new HashMap<>();
        params.put("msg_type",msgType);
        params.put("content",this);
        return params;
    }

    /**
     * text类型
     */
    @Data
    @NoArgsConstructor
    public static class Text extends FeiShuRequest {
        /** text类型 **/
        private String text;

        @Override
        public String getMsgType() {
            return Constants.MSG_TYPE_TEXT;
        }
    }

    /**
     * post 类型
     */
    @Data
    @NoArgsConstructor
    public static class Post extends FeiShuRequest {
        /** post类型 **/
        private String post;

        @Override
        public String getMsgType() {
            return Constants.MSG_TYPE_POST;
        }

        @Override
        public Map<String, Object> getParams() {
            String msgType = this.getMsgType();
            Map<String, Object> params = new HashMap<>();
            params.put("msg_type",msgType);
            params.put("content",this.post);
            return params;
        }
    }

    /**
     * interactive类型
     */
    @Data
    @NoArgsConstructor
    public static class Card extends FeiShuRequest {
        /** interactive 类型 **/
        private String card;

        @Override
        public String getMsgType() {
            return Constants.MSG_TYPE_CARD;
        }

        @Override
        public Map<String,Object> getParams() {
            String msgType = this.getMsgType();
            Map<String, Object> params = new HashMap<>();
            params.put("msg_type",msgType);
            params.put("card",this.card);
            return params;
        }
    }

}
