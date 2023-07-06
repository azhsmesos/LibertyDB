package com.kuaishou.libertydb.common.verify;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class VerifyParam {

    public static void verifyParam(boolean b, String msg) throws IllegalAccessException {
        if (!b) {
            throw new IllegalAccessException(msg);
        }
    }
}
