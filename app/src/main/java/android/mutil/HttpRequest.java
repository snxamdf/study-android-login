package android.mutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        params.put("1", "1");
        params.put("2", "2");
        sendGet("www.baidu.com", params);
    }

    public void asdf() {
        if ("".equals("adsf")) {

        }
        String b=1==2?"a":"b";
        return;
    }

    enum METHOD {
        POST, GET;
    }

    public static String sendGet(String url, Map<String, String> params) {
        return send(url, params, METHOD.GET);
    }

    public static String sendPost(String url, Map<String, String> params) {
        return send(url, params, METHOD.POST);
    }

    public static String send(String url, Map<String, String> params, METHOD method) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (i > 0) {
                sb.append("&");
            }
            i++;
            sb.append(param.getKey()).append("=").append(param.getValue());
        }
        if (method == METHOD.POST) {
            return sendPost(url, sb.toString());
        } else if (method == METHOD.GET) {
            return sendGet(url, sb.toString());
        }
        return null;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */

    private static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            // 打开和URL之间的连接
            URLConnection connection = getURLConnection(urlNameString);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    private static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuffer result = new StringBuffer();
        try {
            // 打开和URL之间的连接
            URLConnection connection = getURLConnection(url);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    private static URLConnection getURLConnection(String url) throws IOException {
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        URLConnection connection = realUrl.openConnection();
        // 设置通用的请求属性
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        connection.setConnectTimeout(1000 * 5);
        connection.setReadTimeout(1000 * 5);
        return connection;
    }
}
