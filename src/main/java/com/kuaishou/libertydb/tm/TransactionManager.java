package com.kuaishou.libertydb.tm;

import static com.kuaishou.libertydb.common.Error.FileCannotRWException;
import static com.kuaishou.libertydb.common.Error.FileExistsException;
import static com.kuaishou.libertydb.common.Error.FileNotExistsException;
import static com.kuaishou.libertydb.tm.TransactionManagerImpl.TX_ID_HEADER_LENGTH;
import static com.kuaishou.libertydb.tm.TransactionManagerImpl.TX_ID_SUFFIX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.kuaishou.libertydb.common.ForceExit;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public interface TransactionManager {

    long begin();

    void commit(long txId);

    void abort(long txId);

    boolean isActive(long txId);

    boolean isCommitted(long txId);

    boolean isAborted(long txId);

    void close(long txId);

    static TransactionManagerImpl create(String path) {
        File file = new File(path + TX_ID_SUFFIX);
        try {
            if (!file.createNewFile()) {
                ForceExit.exit(FileExistsException);
            }
        } catch (Exception e) {
            ForceExit.exit(e);
        }
        if (!file.canRead() || !file.canWrite()) {
            ForceExit.exit(FileCannotRWException);
        }
        FileChannel channel = null;
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "rw");
            channel = accessFile.getChannel();
            ByteBuffer buf = ByteBuffer.wrap(new byte[TX_ID_HEADER_LENGTH]);
            channel.position(0);
            channel.write(buf);
        } catch (IOException e) {
            ForceExit.exit(e);
        }
        return new TransactionManagerImpl(accessFile, channel);
    }

    static TransactionManagerImpl open(String path) {
        File file = new File(path + TX_ID_SUFFIX);
        if (!file.exists()) {
            ForceExit.exit(FileNotExistsException);
        }
        if (!file.canRead() || !file.canWrite()) {
            ForceExit.exit(FileCannotRWException);
        }
        FileChannel channel = null;
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "rw");
            channel = accessFile.getChannel();
        } catch (FileNotFoundException e) {
            ForceExit.exit(e);
        }
        return new TransactionManagerImpl(accessFile, channel);
    }
}
