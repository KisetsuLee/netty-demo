package com.lee.jetty;

import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.*;
import java.security.cert.Certificate;

/**
 * @Author Lee
 * @Date 2021/2/28
 */
public class ExportPrivateKeyFromKeystore {
    private File keystoreFile;
    private String keyStoreType;
    private char[] storePassword;
    private char[] keyPassword;
    private String alias;
    private File exportedFile;

    public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
        try {
            Key key = keystore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                Certificate cert = keystore.getCertificate(alias);
                PublicKey publicKey = cert.getPublicKey();
                return new KeyPair(publicKey, (PrivateKey) key);
            }
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void export() throws Exception {
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        BASE64Encoder encoder = new BASE64Encoder();
        keystore.load(new FileInputStream(keystoreFile), storePassword);
        KeyPair keyPair = getPrivateKey(keystore, alias, keyPassword);

        PrivateKey privateKey = keyPair.getPrivate();
        String encoded = encoder.encode(privateKey.getEncoded());
        FileWriter fw = new FileWriter(exportedFile);
        fw.write("----BEGIN PRIVATE KEY----\n");
        fw.write(encoded);
        fw.write("\n");
        fw.write("----END PRIVATE KEY----\n");

        Certificate cert = keystore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();
        String encoded2 = encoder.encode(publicKey.getEncoded());
        fw.write("----BEGIN CERTIFICATE----\n");
        fw.write(encoded2);
        fw.write("\n");
        fw.write("----END CERTIFICATE----\n");
        fw.close();
    }

    public static void main(String[] args) throws Exception {
        ExportPrivateKeyFromKeystore export = new ExportPrivateKeyFromKeystore();
        export.keystoreFile = new File("F:\\projects\\netty\\src\\main\\resources\\etc\\https.keystore");
        export.keyStoreType = "JKS";
        export.storePassword= "store123".toCharArray();
        export.keyPassword= "key123".toCharArray();
        export.alias = "https";
        export.exportedFile = new File("output");
        export.export();
    }
}
