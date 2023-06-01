package com.demo.chatgptreactor.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by syl nerosyl@live.com on 2023/5/25
 *
 * @author syl
 */
public class JtokkitUtils {


    private static final ReentrantLock LOCK = new ReentrantLock(true);
    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();
    private static final Encoding ENC = REGISTRY.getEncoding(EncodingType.CL100K_BASE);



    public static int countTokens(String text) {

        try {
            if (LOCK.tryLock(150, TimeUnit.MILLISECONDS)) {
                try {
                    return ENC.countTokens(text);
                } finally {
                    LOCK.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncoding(EncodingType.CL100K_BASE);
        return enc.countTokens(text);
    }


    public static void main(String[] args) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding enc = registry.getEncoding(EncodingType.CL100K_BASE);
        System.out.println(enc.countTokens("text"));
    }
}
