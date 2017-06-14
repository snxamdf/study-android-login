import android.mutil.Base64

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import javax.crypto.Cipher

/**
 * Created by hongyanyang1 on 2017/6/13.
 */

class RSAUtils {

    fun test() {
        val encodedString = Base64.encodeToString("whoislcj".toByteArray(), Base64.DEFAULT)
        val decodedString = String(Base64.decode(encodedString, Base64.DEFAULT))
    }

    companion object {

        fun base64EncodedString(key: ByteArray): String {
            return Base64.encodeToString(key, Base64.DEFAULT)
        }

        fun base64DecodedString(key: ByteArray): ByteArray {
            return Base64.decode(key, Base64.DEFAULT)
        }

        fun generateRSAKeyPair(keyLength: Int): KeyPair? {
            try {
                val kpg = KeyPairGenerator.getInstance(RSA)
                kpg.initialize(keyLength)
                return kpg.genKeyPair()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 用公钥对字符串进行加密

         * @param data 原文
         */
        @Throws(Exception::class)
        fun encryptByPublicKey(data: ByteArray, publicKey: ByteArray): ByteArray {
            // 得到公钥
            val keySpec = X509EncodedKeySpec(publicKey)
            val kf = KeyFactory.getInstance(RSA)
            val keyPublic = kf.generatePublic(keySpec)
            // 加密数据
            val cp = Cipher.getInstance(ECB_PKCS1_PADDING)
            cp.init(Cipher.ENCRYPT_MODE, keyPublic)
            return cp.doFinal(data)
        }

        /**
         * 使用私钥进行解密
         */
        @Throws(Exception::class)
        fun decryptByPrivateKey(encrypted: ByteArray, privateKey: ByteArray): ByteArray {
            // 得到私钥
            val keySpec = PKCS8EncodedKeySpec(privateKey)
            val kf = KeyFactory.getInstance(RSA)
            val keyPrivate = kf.generatePrivate(keySpec)
            // 解密数据
            val cp = Cipher.getInstance(ECB_PKCS1_PADDING)
            cp.init(Cipher.DECRYPT_MODE, keyPrivate)
            val arr = cp.doFinal(encrypted)
            return arr
        }

        val RSA = "RSA"// 非对称加密密钥算法
        val ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"//加密填充方式
        val DEFAULT_KEY_SIZE = 2048//秘钥默认长度

        @JvmStatic fun main(args: Array<String>) {
            val keyPair = RSAUtils.generateRSAKeyPair(RSAUtils.DEFAULT_KEY_SIZE)
            // 生成公钥
            val publicKey = keyPair!!.public as RSAPublicKey
            println(base64EncodedString(publicKey.encoded))
            // 生成私钥
            val privateKey = keyPair.private as RSAPrivateKey
            println(base64EncodedString(privateKey.encoded))

            try {
                //测试
                val o = "aaaaaaaaaaaa"
                val enr = encryptAndBase64Encode(o.toByteArray())//加密后转base64
                val dec = decryptAndBase64Decode(enr!!.toByteArray())//解base64后解密
                val o2 = String(dec!!)
                println(o2)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        //加密 字符串先rsa encrypt 后 Base64Decode
        internal fun encryptAndBase64Encode(src: ByteArray): String? {
            try {
                return RSAUtils.base64EncodedString(RSAUtils.encryptByPublicKey(src, RSAUtils.base64DecodedString(pubKey.toByteArray())))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        //解密 字符串先Base64Decode 后 rsa decrypt
        internal fun decryptAndBase64Decode(src: ByteArray): ByteArray? {
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
}
