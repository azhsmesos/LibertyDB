package com.kuaishou.libertydb.dm.pageCache;

import static com.kuaishou.libertydb.common.Error.MemTooSmallException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.kuaishou.libertydb.common.ForceExit;
import com.kuaishou.libertydb.common.cache.AbstractCache;
import com.kuaishou.libertydb.dm.page.Page;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class PageCacheImpl extends AbstractCache<Page> implements PageCache {

    private static final int MEM_MIN_LEN = 10;

    public static final String DB_SUFFIX = ".db";

    private RandomAccessFile accessFile;

    private FileChannel channel;

    private Lock lock;

    // 记录这个打开的数据库文件有多少页
    private AtomicInteger pageNumbers;

    public PageCacheImpl(RandomAccessFile accessFile, FileChannel channel, int maxResource) {
        super(maxResource);
        if (maxResource < MEM_MIN_LEN) {
            ForceExit.exit(MemTooSmallException);
        }
        long length = 0;
        try {
            length = accessFile.length();
        } catch (IOException e) {
            ForceExit.exit(e);
        }
        this.accessFile = accessFile;
        this.channel = channel;
        this.lock = new ReentrantLock();
        this.pageNumbers = new AtomicInteger((int) length / PAGE_SIZE);
    }

    @Override
    protected Page getForCache(long key) throws Exception {
        return null;
    }

    @Override
    protected void releaseForCache(Page obj) {

    }

    @Override
    public int newPage(byte[] initData) {
        return 0;
    }

    @Override
    public Page getPage(int pageNo) throws Exception {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void release(Page page) {

    }

    @Override
    public void truncateByPageNo(int maxPageNo) {

    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public void flushPage(Page page) {

    }
}
