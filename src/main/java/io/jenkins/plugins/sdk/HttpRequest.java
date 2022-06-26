package io.jenkins.plugins.sdk;


import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

@Data
@Builder
public class HttpRequest {

    /** 默认字符集 **/
    private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;

    /** 忽略ssl检查 **/
    private static boolean IGNORE_SSL_CHECK = true;

    private static boolean IGNORE_HOST_CHECK = true;

    /** webhook地址 **/
    private String server;

    /** 默认请求方法 **/
    @Default
    private String method = Constants.METHOD_GET;

    /** 请求体类型 **/
    @Default
    private String contentType = Constants.CONTENT_TYPE_APPLICATION_JSON;

    /** 报文头 **/
    private Map<String, String> header;

    /** url 参数 **/
    private Map<String, String> params;

    /** body 参数 **/
    private Map<String, Object> data;

    /** 代理 **/
    private Proxy proxy;

    @Default
    private String charset = DEFAULT_CHARSET;

    /**
     * 自定义信任管理器
     **/
    private static class TrustAllTrustManager implements X509TrustManager {

        /**
         * 检查客户端信任证书是否可信
         * @param x509Certificates 证书列表
         * @param s
         * @throws CertificateException
         */
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        /**
         * 检查服务端证书是否可信
         * @param x509Certificates 证书列表
         * @param s
         * @throws CertificateException
         */
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        /**
         * 获取受信任证书列表
         * @return 信任证书列表
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    /**
     * 获取http连接
     * @param url url
     * @param method 请求方法
     * @param contentType 请求类型
     * @param headerMap 请求头
     * @return http/https连接
     * @throws IOException
     */
    private static HttpURLConnection getConnection(URL url, String method, String contentType, Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn;

        conn = (HttpURLConnection) url.openConnection();

        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection connHttps = (HttpsURLConnection) conn;
            if (IGNORE_SSL_CHECK) {
                try {
                    //获取sslctx实例
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    ctx.init(null,new TrustManager[]{new TrustAllTrustManager()}, new SecureRandom());
                    ((HttpsURLConnection) conn).setSSLSocketFactory(ctx.getSocketFactory());
                    connHttps.setHostnameVerifier((hostname, session) -> true);
                } catch (Exception e) {
                    throw new IOException(e.toString());
                }
            } else {
                if (IGNORE_HOST_CHECK) {
                    connHttps.setHostnameVerifier((hostname, session) -> true);
                }
            }
            conn = connHttps;
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Host", url.getHost());
        conn.setRequestProperty("Content-Type", contentType);
        if (headerMap != null) {
            for (Map.Entry<String,String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }
        }
        return conn;
    }

    /**
     * http请求方法
     * @return 请求结果
     * @throws IOException
     */
    public HttpResponse request() throws IOException {
        String rsp;
        HttpURLConnection conn = null;
        OutputStream out = null;
        HttpResponse response = new HttpResponse();
        try {
            String query = buildQuery(params, charset);
            URL url = new URL(StringUtils.isEmpty(null) ? server : buildRequestUrl(server, query));
            byte[] body = buildBody(data,charset);
            conn = getConnection(url,method,contentType,header);
            conn.setRequestMethod(Constants.METHOD_POST);
            out = conn.getOutputStream();
            out.write(body);
            rsp = getResponseAsString(conn);
            response.setBody(rsp);
            Map<String, List<String>> headers = conn.getHeaderFields();
            response.setHeaders(headers);
        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }

    public static String buildRequestUrl(String url, String... queries) {
        if (queries == null || queries.length == 0) {
            return url;
        }

        StringBuilder newUrl = new StringBuilder(url);
        boolean hasQuery = url.contains("?");
        boolean hasPrepend = url.endsWith("?") || url.endsWith("&");

        for (String query : queries) {
            if (StringUtils.isNotEmpty(query)) {
                if (!hasPrepend) {
                    if (hasQuery) {
                        newUrl.append("&");
                    } else {
                        newUrl.append("?");
                        hasQuery = true;
                    }
                }
                newUrl.append(query);
                hasPrepend = false;
            }
        }
        return newUrl.toString();
    }

    public static byte[] buildBody(Object data, String charset) throws UnsupportedEncodingException {
        if (data == null) {
            return new byte[] {};
        }
        String body;
        if (data instanceof String) {
            body = (String) data;
        } else {
            body = JSON.toJSONString(data);
        }
        return body.getBytes(charset);
    }

    public static String buildQuery(Map<String, String> params, String charset) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String,String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Map.Entry<String,String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                query.append(name).append("=").append(URLEncoder.encode(value,charset));
            }
        }

        return query.toString();
    }

    private static String getResponseAsString(HttpURLConnection conn) throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            String contentEncoding = conn.getContentEncoding();
            if (Constants.CONTENT_ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
                return getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset);
            } else {
                return getStreamAsString(conn.getInputStream(), charset);
            }
        } else {
            // OAuth bad request always return 400 status
            if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStream error = conn.getErrorStream();
                if (error != null) {
                    return getStreamAsString(error, charset);
                }
            }
            // Client Error 4xx and Server Error 5xx
            throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
        }
    }

    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public static String getResponseCharset(String contentType) {
        String charset = DEFAULT_CHARSET;

        if (StringUtils.isNotEmpty(contentType)) {
            String[] params = contentType.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

}
