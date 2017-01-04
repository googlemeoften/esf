package cn.edu.esf;

import cn.edu.esf.utils.StringUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * esf://cn.edu.esf.HelloService?class = java;
 *
 * @Author heyong
 * @Date 2016/12/21
 */
public class RemotingURL {
    private static final ConcurrentHashMap<String, SoftReference<RemotingURL>> parseUrls = new ConcurrentHashMap<>();

    private final String url;
    private final String protocol;
    private final String host;
    private final int port;
    private final String path;
    private final Map<String, String> parameters;

    public RemotingURL(String url, String protocol, String host, int port, String path, Map<String, String> parameters) {
        this.url = url;
        this.protocol = protocol;
        this.host = host;
        this.port = (port < 0 ? 0 : port);
        this.path = path;
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        if (parameters == null) {
            this.parameters = new HashMap<>();
        } else {
            this.parameters = parameters;
        }
    }

    public static RemotingURL parseUrl(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String fullStr = url;
        SoftReference<RemotingURL> remotingURL = parseUrls.get(url);
        if (remotingURL != null) {
            RemotingURL cacheURL = remotingURL.get();
            if (cacheURL != null) {
                return cacheURL;
            }
        }
        String host = null;
        int port = 0;
        String protocol = null;
        String path = null;
        Map<String, String> parameters = new HashMap<>();

        int index = url.indexOf("?");
        if (index >= 0) {
            String[] parts = url.substring(index + 1).split("\\&");
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int i = part.indexOf("=");
                    if (i > 0) {
                        parameters.put(part.substring(0, i), part.substring(i + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, index);
        }
        index = url.indexOf("://");
        if (index >= 0) {
            if (index == 0) {
                throw new IllegalArgumentException("URL miss protocol");
            }
            protocol = url.substring(0, index);
            url = url.substring(index + 3);
        } else {
            //case: file:/path/file.txt
            index = url.indexOf(":/");
            if (index >= 0) {
                protocol = url.substring(0, index);
                url = url.substring(index + 2);
            }
        }

        index = url.indexOf("/");
        if (index >= 0) {
            path = url.substring(index + 1);
            url = url.substring(0, index);
        }

        index = url.indexOf(":");
        if (index >= 0) {
            port = Integer.parseInt(url.substring(index + 1));
            url = url.substring(0, index);
        }

        if (url.length() > 0) {
            host = url;
        }
        RemotingURL parsedURL = new RemotingURL(fullStr, protocol, host, port, path, parameters);
        parseUrls.put(fullStr, new SoftReference<RemotingURL>(parsedURL));

        return parsedURL;
    }


    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public int getParameter(String parameterName, int defaultValue) {
        String stringValue = this.getParameter(parameterName);
        if (StringUtils.isBlank(stringValue)) {
            return defaultValue;
        } else {
            return Integer.valueOf(stringValue);
        }
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

}
