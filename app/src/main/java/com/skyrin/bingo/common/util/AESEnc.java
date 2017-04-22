package com.skyrin.bingo.common.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEnc {
    private final String KEY_ALGORITHM;
    private SecretKey aesKey;
    private byte[] key;
    private String mode;

    public AESEnc() {
        this.mode = "AES/ECB/PKCS5Padding";
        this.KEY_ALGORITHM = "AES";
        this.key = "]^8tSuSZn1+-sm_+L%tgMmSmn=tTt3%s".getBytes();
        this.aesKey = new SecretKeySpec(this.key, "AES");
    }

    public AESEnc(byte[] bArr) {
        this.mode = "AES/ECB/PKCS5Padding";
        this.KEY_ALGORITHM = "AES";
        this.aesKey = new SecretKeySpec(bArr, "AES");
    }

    private byte[] generateAesKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(256);
        return keyGenerator.generateKey().getEncoded();
    }

    public byte[] encrypt(byte[] bArr) throws Exception {
        try {
            Cipher instance = Cipher.getInstance(this.mode);
            instance.init(1, this.aesKey);
            return instance.doFinal(bArr);
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] decrypt(byte[] bArr) throws Exception {
        try {
            Cipher instance = Cipher.getInstance(this.mode);
            instance.init(2, this.aesKey);
            return instance.doFinal(bArr);
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] getKey() {
        return this.key;
    }
}
