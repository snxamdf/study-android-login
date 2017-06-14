package com.yhy.login

import android.app.ProgressDialog
import android.mutil.HttpRequest
import android.mutil.RSAUtils
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    var gson = GsonBuilder().create()
    var configName: String = "intranetConfig.xml"
    var url: String = "http://10.10.38.143/portal/addisclaimer/pc.html?template=addisclaimer&amp;tabs=pwd-sms"
    var loginService: String = "http://10.10.38.143/portal/login.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //var builder = AlertDialog.Builder(this)
        btnLogin.setOnClickListener {
            val eName = eName.text;
            val ePasswd = ePasswd.text;
            if (isEmpty(eName)) {
                //builder.setTitle("提示").setMessage("请输入您的用户名！")
                //Toast.makeText(this, "请输入您的用户名！", Toast.LENGTH_SHORT).show()
                toast("请输入您的用户名！")
                tvMessage.text = "请输入您的用户名！";
            } else if (isEmpty(ePasswd)) {
                //Toast.makeText(this, "请输入您的密码！", Toast.LENGTH_SHORT).show()
                toast("请输入您的密码！")
                tvMessage.text = "请输入您的密码！";
            } else {
                tvMessage.text = "正在登录...";
                val conf = Config()
                conf.loginName = eName.toString()
                conf.passwd = encryptAndBase64Encode(ePasswd.toString().toByteArray())!!
                autoLogin(conf, false)
                //Toast.makeText(this, result!!.msg, Toast.LENGTH_SHORT).show()
            }
        }

        var conf = readConf()
        autoLogin(conf, true)
    }

    fun autoLogin(conf: Config?, isAuto: Boolean) {
        val dialog = ProgressDialog(this)
        dialog.setTitle("登录提示")
        dialog.setMessage("正在登录请稍后...")
        dialog.show()
        async {
            var result = login(conf)
            uiThread {
                dialog.cancel()
                val autoMsg = if (isAuto) "自动" else ""
                if (conf != null) {
                    eName.setText(conf.loginName)
                    ePasswd.setText(String(decryptAndBase64Decode(conf.passwd.toByteArray())!!))
                }
                if (result == null || !result.success) {
                    val b = if (1 == 2) "a" else "b"
                    tvMessage.text = "本次" + autoMsg + "登录失败 ！";
                } else {
                    tvMessage.text = "本次" + autoMsg + "登录成功！";
                }
                toast(result?.msg ?: "本次" + autoMsg + "登录失败！")
            }
        }
    }

    /**
     * 登录
     */
    fun login(conf: Config?): Result? {
        if (conf == null) {
            return null
        }
        var loginName = conf.loginName
        var passwd = String(decryptAndBase64Decode(conf.passwd.toByteArray())!!)
        val params = HashMap<String, String>()
        params.put("rememberPwd", "0");
        params.put("opr", "pwdLogin");
        params.put("userName", loginName);
        params.put("pwd", passwd);
        var result = HttpRequest.sendPost(loginService, params);
        result = result.replace("<br>", "，")
        this.writeConf(conf)
        var r = gson.fromJson(result, Result::class.java)
        return r;
    }


    /**
     *输出配置
     */
    fun writeConf(conf: Config) {
        var file = this.getFile()
        var json = this.gson.toJson(conf)
        file.writeText(json)
    }

    /**
     *读取配置
     */
    fun readConf(): Config? {
        var file = this.getFile()
        var json = file.readText()
        if (json == null || "".equals(json)) {
            return null
        }
        try {
            var conf = this.gson.fromJson(json, Config::class.java)
            return conf
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 获取文件对像
     */
    fun getFile(): File {
        var curDir = applicationContext.filesDir.path.toString() + "/login/" + this.configName
        var file = File(curDir)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        return file
    }

    //加密 字符串先rsa encrypt 后 Base64Decode
    fun encryptAndBase64Encode(src: ByteArray): String? {
        try {
            return RSAUtils.base64EncodedString(RSAUtils.encryptByPublicKey(src, RSAUtils.base64DecodedString(pubKey.toByteArray())))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //解密 字符串先Base64Decode 后 rsa decrypt
    fun decryptAndBase64Decode(src: ByteArray): ByteArray? {
        try {
            return RSAUtils.decryptByPrivateKey(RSAUtils.base64DecodedString(src), RSAUtils.base64DecodedString(priKey.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private val pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlaSVahwldva5mvxY9I2uImKO5TZA/cnc\n" +
            "6MFEZnBW1NbUlq85M7sjHI8Lm/NXKtvy8yF114jUvZhU91DrfC1PfndMbJCl9YW+qFVz42r4E6Oa\n" +
            "T5+fAJAUWV5tXR2SWYvjaL7TZUuCNK/0DZbfyTqgPasTD1XlO6uEHRBuPyfoC0QuW/xbvrBFWjoh\n" +
            "RINUnozGaiEnS2eij17X6Pll3g+h9hx81Owvpnx1EhyUP4rrs53xILbWGpxtnEI2n2B/Vs0ffpnD\n" +
            "hUDmIdwfiWSX9OSxoHn1NRDunRJfl0T3OkYr3gAzrudkTBddLF4Ebzye3g83H05uj8C2T2GkaY+l\n" +
            "+1vzKwIDAQAB"

    private val priKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVpJVqHCV29rma/Fj0ja4iYo7l\n" +
            "NkD9ydzowURmcFbU1tSWrzkzuyMcjwub81cq2/LzIXXXiNS9mFT3UOt8LU9+d0xskKX1hb6oVXPj\n" +
            "avgTo5pPn58AkBRZXm1dHZJZi+NovtNlS4I0r/QNlt/JOqA9qxMPVeU7q4QdEG4/J+gLRC5b/Fu+\n" +
            "sEVaOiFEg1SejMZqISdLZ6KPXtfo+WXeD6H2HHzU7C+mfHUSHJQ/iuuznfEgttYanG2cQjafYH9W\n" +
            "zR9+mcOFQOYh3B+JZJf05LGgefU1EO6dEl+XRPc6RiveADOu52RMF10sXgRvPJ7eDzcfTm6PwLZP\n" +
            "YaRpj6X7W/MrAgMBAAECggEBAISo0niuGRx8n5BhU68BhzUecJWM4lLayMdixnOV9bRb+zzWe/x7\n" +
            "UyY3PdB0CnuJX7jgmeqIeCjYScKybwC33ng75Hl+RlIBzkLG9qTOqLwoVl1uIXRLRm7vwj5BQAO4\n" +
            "etLaEOgE55ozvkTp0tw+592jspLuz/h1Ffr6HPJKO3D4DN1MdYHvGMJFOQFzdpyJO68YcB2uYc1t\n" +
            "mwogzV6Hk5r45iFZBERcEA/mcx/XrCO8IpWV3iMBZ4EiGQQfzJfYKR0/JrOB58ruItBYZPyIn6Iw\n" +
            "Z8zC6XIJgG4HduuYAf+MDqXBsbC9OxlEOG3meKFyEcVmBvsyH3RE3rmiPq60UPECgYEA+Aq7Dp60\n" +
            "+3Jo92zOjw+EgPuqAtGTSHReY3ti6lfKcdIksEPCGTRFNKGZgq0CwrQTajDbfZdkNYuOgQ04f8+7\n" +
            "OP3NWdQ4JpbZ+g8oSNQMx7uQzqDipq4TeQJAjpwwNk3EFPYMBB2g5Gn7F9lfeKBpFXmeqfvptw7U\n" +
            "kWrEp5Dx1PMCgYEAmnGpav0pG0E1COg7ZFETjyI+w2e7EjEJJpVpk3U4/svxD7sTgvrTkReRsoX+\n" +
            "FdVillVsfnW/S6GRmYLEFTC/a2mAWP0mXIfHZhEgoJ9jjDRWiVTa6LSYX567x8Hq+3ThZf6O6WUy\n" +
            "pY0hfOquPgWW2ptzo2JSWio/3zcHw4NK1ukCgYBLV98YAsdQtaECvy9DL2B9WXR75LMLSCW/rCQQ\n" +
            "sNgSmNWCISLdSw5WfVvG4My83bwj/nE9hfXvedOwiZaG5E+ncRimV5syxZGyrlX7QUYciXHkAeS2\n" +
            "4puRn0iCyRiv9hFAmLhvq5xKpZKa3PFuD7O7zTSPx7BnZX7WKQtRJur+VwKBgHEP7k+1fydFqDaa\n" +
            "FAiPVfs9vaa9RHS/0wwc60oY0Z2t3Q6ADHuhdcpM78s6TlTbfq3BYYh+WIlcgUNZOISuyCMw+9Wp\n" +
            "lTC98ZplxXXw2SZllkg5B3y94KJ3iM5mxshIu004epSgEeCiHbbd8qrS2qm0jYY5T0JUlaeqGJPn\n" +
            "hJ0pAoGAH/D5UbgBkq2BGa8WY7IlzSadTB/hWXzdUBkxMqeUNPBhIo6EQ31keLV1G4LCbkd9lnnp\n" +
            "8OWwFOp7uo38YTythKGDqv6DYnQsxwkxRtXpFn5A+6Z++F1uk+2hUVqCIKXK7ZSrmReDWy2W396J\n" +
            "2tTZM0iOHU2kGOGIUfis1DX15NQ="

}

/**
 *配置类
 */
class Config {
    var loginName: String = ""
    var passwd: String = ""
}

//{'success':false,'msg':'用户名：hongyanyang1@creditease.cn<br>IP地址：10.10.144.104<br>该IP已登录，请先注销','action':'location',pop:0,'userName':'asdf','location':''}
class Result {
    var success: Boolean = false
    var msg: String = ""
}