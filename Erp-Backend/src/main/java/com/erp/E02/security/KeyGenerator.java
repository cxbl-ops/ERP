package com.erp.E02.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // 生成 512 位的 HS512 密钥
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // 转换为 Base64 字符串（用于配置文件）
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("JWT Secret Key (Base64): " + base64Key);
    }
}