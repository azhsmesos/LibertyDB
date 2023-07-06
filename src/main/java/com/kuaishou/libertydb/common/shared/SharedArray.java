package com.kuaishou.libertydb.common.shared;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class SharedArray {

    public byte[] raw;

    public int start;

    public int end;

    public SharedArray(byte[] raw, int start, int end) {
        this.raw = raw;
        this.start = start;
        this.end = end;
    }
}
