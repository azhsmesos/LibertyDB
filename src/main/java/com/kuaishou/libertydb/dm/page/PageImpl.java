package com.kuaishou.libertydb.dm.page;

import java.util.concurrent.locks.Lock;

import com.kuaishou.libertydb.dm.pageCache.PageCache;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class PageImpl implements Page {

    private int pageNumber;

    private byte[] data;

    private boolean dirty;

    private Lock lock;

    private PageCache pc;
}
