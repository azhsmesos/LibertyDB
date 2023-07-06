package com.kuaishou.libertydb.dm.pageCache;

import com.kuaishou.libertydb.dm.page.Page;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public interface PageCache {

    int PAGE_SIZE = 1 << 13;

    int newPage(byte[] initData);

    Page getPage(int pageNo) throws Exception;

    void close();

    void release(Page page);

    void truncateByPageNo(int maxPageNo);

    int getPageNumber();

    void flushPage(Page page);


}
