package com.kuaishou.libertydb.common;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class ForceExit {

    public static void exit(Exception err) {
        err.printStackTrace();
        System.exit(1);
    }
}
