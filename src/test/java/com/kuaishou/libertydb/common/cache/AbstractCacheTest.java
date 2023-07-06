package com.kuaishou.libertydb.common.cache;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class AbstractCacheTest {

    @Test
    public void get() throws Throwable {
        AbstractCache cache = new AbstractCache(100) {
            @Override
            protected Object getForCache(long key) throws Exception {
                return null;
            }

            @Override
            protected void releaseForCache(Object obj) {

            }
        };
        Object obj = cache.get(10);
        System.out.println(obj);
    }

    @Test
    public void release() {
    }

    @Test
    public void close() {
    }
}