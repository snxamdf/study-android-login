import android.mutil.HttpRequest
import android.util.Base64
import java.io.File
import java.io.IOException
import com.google.gson.GsonBuilder


/**
 * Created by hongyanyang1 on 2017/6/8.
 */

object TestMain {
    @JvmStatic fun main(args: Array<String>) {
        var gson = GsonBuilder().create()
        var result = "{'success':false,'msg':'用户名：hongyanyang1@creditease.cn<br>IP地址：10.10.144.104<br>该IP已登录，请先注销','action':'location',pop:0,'userName':'asdf','location':''}"
        result = result.replace("<br>", "，")
        var r = gson.fromJson(result, Result::class.java)
        println(r.success)
        println(r.msg)
    }
}

//{'success':false,'msg':'用户名：hongyanyang1@creditease.cn<br>IP地址：10.10.144.104<br>该IP已登录，请先注销','action':'location',pop:0,'userName':'asdf','location':''}
class Result {
    var success: Boolean = false
    var msg: String = ""
}
