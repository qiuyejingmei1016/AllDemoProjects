package com.newrecord.cloud.utils;

import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * @describe: rsa加解密封装类
 * @author: yyh
 * @createTime: 2019/7/23 11:43
 * @className: RSAUtils
 */
public class RSAUtils1 {

    /**
     * 加密算法RSA
     */
    private static final String RSA = "RSA";

    /**
     * 和后台调试了大半天总是鉴权失败，查找很多资料，甚至和后台java代码对比，也发现不了到底有什么不一样。
     * 问题的根因在于android底层还是和原生的java有很多的区别。根因就在于这句代码：
     * Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
     * 在获取Cipher的实例时，参数改成RSA/ECB/PKCS1Padding即可。
     * 原因就是此处如果写成"RSA"加密出来的信息JAVA服务器无法解析 且每次加密出来的密文都是一样的（RSA算法加密出来的算法应该是每次都不一样）
     */
//    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";//加密填充方式
    private static final String RSA_OAEP_SHA256_PADDING = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";//加密填充方式


    /**
     * 用公钥对字符串进行加密
     *
     * @param data 数据
     */
    @SuppressLint("TrulyRandom")
    public static byte[] RSA_PublicEncrypt(byte[] data, byte[] publicKey) {
        try {
            byte[] decodePublicKey = Base64.decode(publicKey, Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodePublicKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            RSAPublicKey keyPublic = (RSAPublicKey) kf.generatePublic(keySpec);
            int keySize = keyPublic.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keyPublic);
            return rsaSplitCodec(Cipher.ENCRYPT_MODE, data, keySize, cipher);
        } catch (Exception e) {
            Log.e("AnyChatCertHelper", "RSA_PublicEncrypt failure", e.fillInStackTrace());
        }
        return null;
    }


    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return byte[] 加密数据
     */
    @SuppressLint("TrulyRandom")
    public static byte[] RSA_PrivateEncrypt(byte[] data, byte[] privateKey) {
        try {
            byte[] key = getKey(privateKey);
            byte[] decodePrivateKey = Base64.decode(key, Base64.NO_WRAP);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodePrivateKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            RSAPrivateKey keyPrivate = (RSAPrivateKey) kf.generatePrivate(keySpec);
            int keySize = keyPrivate.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
            return rsaSplitCodec(Cipher.ENCRYPT_MODE, data, keySize, cipher);
        } catch (Exception e) {
            Log.e("AnyChatCertHelper", "RSA_PrivateEncrypt failure", e.fillInStackTrace());
        }
        return null;
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     * @return byte[] 解密数据
     */
    @SuppressLint("TrulyRandom")
    public static byte[] RSA_PublicDecrypt(byte[] data, byte[] publicKey) {
        try {
            byte[] decodePublicKey = Base64.decode(publicKey, Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodePublicKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            RSAPublicKey keyPublic = (RSAPublicKey) kf.generatePublic(keySpec);
            int keySize = keyPublic.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keyPublic);
            return rsaSplitCodec(Cipher.DECRYPT_MODE, data, keySize, cipher);
        } catch (Exception e) {
            Log.e("AnyChatCertHelper", "RSA_PublicDecrypt failure", e.fillInStackTrace());
        }
        return null;
    }

    /**
     * 使用私钥进行解密
     */
    @SuppressLint("TrulyRandom")
    public static byte[] RSA_PrivateDecrypt(byte[] data, byte[] privateKey) {
        try {
            byte[] key = getKey(privateKey);
            byte[] decodePrivateKey = Base64.decode(key, Base64.NO_WRAP);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodePrivateKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            RSAPrivateKey keyPrivate = (RSAPrivateKey) kf.generatePrivate(keySpec);
            int keySize = keyPrivate.getModulus().bitLength();
            Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keyPrivate);
            return rsaSplitCodec(Cipher.DECRYPT_MODE, data, keySize, cipher);
        } catch (Exception e) {
            Log.e("AnyChatCertHelper", "RSA_PrivateDecrypt failure", e.fillInStackTrace());
        }
        return null;
    }

    /**
     * 对字符串进行分段加密或解密
     */
    private static byte[] rsaSplitCodec(int mode, byte[] data, int keySize, Cipher cipher) throws Exception {
        int dataLen = data.length;
        int maxBlock;
        if (mode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = (keySize / 8) - 66;
        }
        if (dataLen <= maxBlock) {
            return cipher.doFinal(data);
        }
        List<Byte> allBytes = new ArrayList<Byte>();
        int bufIndex = 0;
        byte[] buf = new byte[maxBlock];
        for (int i = 0; i < dataLen; i++) {
            buf[bufIndex] = data[i];
            if ((++bufIndex == maxBlock) || (i == dataLen - 1)) {
                byte[] encryptBytes = cipher.doFinal(buf);
                for (byte b : encryptBytes) {
                    allBytes.add(b);
                }
                bufIndex = 0;
                if (i == dataLen - 1) {
                    buf = null;
                } else {
                    buf = new byte[Math.min(maxBlock, dataLen - i - 1)];
                }
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b.byteValue();
            }
        }
        return bytes;
    }


    private static byte[] getKey(byte[] s) {
        String keyContent = new String(s);
        String key = keyContent.substring(keyContent.indexOf("Y-----") + 7, keyContent.lastIndexOf("-----E"));
        return key.getBytes();
    }
}
