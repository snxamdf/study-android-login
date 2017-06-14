import android.os.Build
import android.support.annotation.RequiresApi
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URL
import java.net.URLConnection
import java.util.*

class HttpRequest {

    fun asdf() {
        if ("" == "adsf") {

        }
        val b = if (1 == 2) "a" else "b"
        return
    }

    enum class METHOD {
        POST, GET
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val params = HashMap<String, String>()
            params.put("1", "1")
            params.put("2", "2")
            sendGet("www.baidu.com", params)
        }

        fun sendGet(url: String, params: Map<String, String>): String {
            return send(url, params, METHOD.GET)!!
        }

        fun sendPost(url: String, params: Map<String, String>): String {
            return send(url, params, METHOD.POST)!!
        }

        fun send(url: String, params: Map<String, String>, method: METHOD): String? {
            val sb = StringBuffer()
            var i = 0
            for ((key, value) in params) {
                if (i > 0) {
                    sb.append("&")
                }
                i++
                sb.append(key).append("=").append(value)
            }
            if (method == METHOD.POST) {
                return sendPost(url, sb.toString())
            } else if (method == METHOD.GET) {
                return sendGet(url, sb.toString())
            }
            return null
        }

        /**
         * 向指定URL发送GET方法的请求

         * @param url   发送请求的URL
         * *
         * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
         * *
         * @return URL 所代表远程资源的响应结果
         */

        private fun sendGet(url: String, param: String): String {
            val result = StringBuffer()
            var `in`: BufferedReader? = null
            try {
                val urlNameString = url + "?" + param
                // 打开和URL之间的连接
                val connection = getURLConnection(urlNameString)
                // 建立实际的连接
                connection.connect()
                // 获取所有响应头字段
                val map = connection.headerFields
                // 遍历所有的响应头字段
                for (key in map.keys) {
                    println(key + "--->" + map[key])
                }
                // 定义 BufferedReader输入流来读取URL的响应
                `in` = BufferedReader(InputStreamReader(connection.getInputStream()))
                `in`.forEachLine {
                    result.append(it)
                }
            } catch (e: Exception) {
                println("发送GET请求出现异常！" + e)
                e.printStackTrace()
            } finally {
                try {
                    if (`in` != null) {
                        `in`.close()
                    }
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }

            }// 使用finally块来关闭输入流
            return result.toString()
        }

        /**
         * 向指定 URL 发送POST方法的请求

         * @param url   发送请求的 URL
         * *
         * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
         * *
         * @return 所代表远程资源的响应结果
         */
        private fun sendPost(url: String, param: String): String {
            var out: PrintWriter? = null
            var `in`: BufferedReader? = null
            val result = StringBuffer()
            try {
                // 打开和URL之间的连接
                val connection = getURLConnection(url)
                // 发送POST请求必须设置如下两行
                connection.doOutput = true
                connection.doInput = true
                // 获取URLConnection对象对应的输出流
                out = PrintWriter(connection.getOutputStream())
                // 发送请求参数
                out.print(param)
                // flush输出流的缓冲
                out.flush()
                // 定义BufferedReader输入流来读取URL的响应
                `in` = BufferedReader(InputStreamReader(connection.getInputStream()))
                `in`.forEachLine {
                    result.append(it)
                }
            } catch (e: Exception) {
                println("发送 POST 请求出现异常！" + e)
                e.printStackTrace()
            } finally {
                try {
                    if (out != null) {
                        out.close()
                    }
                    if (`in` != null) {
                        `in`.close()
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

            }//使用finally块来关闭输出流、输入流
            return result.toString()
        }

        @Throws(IOException::class)
        private fun getURLConnection(url: String): URLConnection {
            val realUrl = URL(url)
            // 打开和URL之间的连接
            val connection = realUrl.openConnection()
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*")
            connection.setRequestProperty("connection", "Keep-Alive")
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
            connection.connectTimeout = 1000 * 5
            connection.readTimeout = 1000 * 5
            return connection
        }
    }
}
