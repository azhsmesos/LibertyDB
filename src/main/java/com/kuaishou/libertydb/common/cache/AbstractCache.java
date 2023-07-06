package com.kuaishou.libertydb.common.cache;

import static com.kuaishou.libertydb.common.Error.CacheFullException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Maps;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public abstract class AbstractCache<T> {

    // 实际缓存的数据
    private Map<Long, T> cache;

    // 元素引用个数
    private Map<Long, Integer> references;

    // 正在获取资源的线程
    private Map<Long, Boolean> busyThreads;

    // 缓存的最大缓存量
    private int maxResource;

    // 缓存中元素个数
    private int count = 0;

    private Lock lock;

    public AbstractCache(int maxResource) {
        this.maxResource = maxResource;
        cache = Maps.newHashMap();
        references = Maps.newHashMap();
        busyThreads = Maps.newHashMap();
        lock = new ReentrantLock();
    }

    protected T get(long key) throws Exception {
        for (; ; ) {
            lock.lock();
            if (busyThreads.containsKey(key)) {
                lock.unlock();
                Thread.sleep(1);
                continue;
            }

            if (cache.containsKey(key)) {
                T obj = cache.get(key);
                references.put(key, references.get(key) + 1);
                lock.unlock();
                return obj;
            }

            if (maxResource > 0 && count == maxResource) {
                lock.unlock();
                throw CacheFullException;
            }

            count++;
            busyThreads.put(key, true);
            lock.unlock();
            break;
        }
        T obj = null;
        try {
            obj = getForCache(key);
        } catch (Exception e) {
            lock.lock();
            count--;
            busyThreads.remove(key);
            lock.unlock();
            throw e;
        }
        lock.lock();
        busyThreads.remove(key);
        cache.put(key, obj);
        references.put(key, 1);
        lock.unlock();
        return obj;
    }

    protected void release(long key) {
        lock.lock();
        try {
            int ref = references.get(key);
            if (ref == 0) {
                T obj = cache.get(key);
                releaseForCache(obj);
                references.remove(key);
                cache.remove(key);
                count--;
            }
        } finally {
            lock.unlock();
        }
    }

    protected void close() {
        lock.lock();
        try {
            Set<Long> keys = cache.keySet();
            keys.forEach(it -> {
                T obj = cache.get(it);
                releaseForCache(obj);
                references.remove(it);
                cache.remove(it);
            });
        } finally {
            lock.unlock();
        }
    }


    protected abstract T getForCache(long key) throws Exception;

    protected abstract void releaseForCache(T obj);
}
