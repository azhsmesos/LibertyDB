package com.kuaishou.libertydb.tm;

import static com.kuaishou.libertydb.common.Error.BadXIDFileException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.kuaishou.libertydb.common.ForceExit;
import com.kuaishou.libertydb.common.util.Parser;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class TransactionManagerImpl implements TransactionManager {

    // txId 文件头长度
    static final int TX_ID_HEADER_LENGTH = 8;

    // 每个事物的占用长度
    private static final int TX_ID_FIELD_SIZE = 1;

    // 事物三种状态
    private static final byte FIELD_TX_ACTIVE = 0;
    private static final byte FIELD_TX_COMMITTED = 0;
    private static final byte FIELD_TX_ABORTED = 0;

    // 超级事物
    public static final long SUPER_TX_ID = 0;

    static final String TX_ID_SUFFIX = ".xid";

    private RandomAccessFile accessFile;

    private FileChannel channel;

    private long txIdCounter;

    private Lock counterLock;

    TransactionManagerImpl(RandomAccessFile accessFile, FileChannel channel) {
        this.accessFile = accessFile;
        this.channel = channel;
        counterLock = new ReentrantLock();
        // 校验txId文件是否合法
        checkFileIsCorruption();
    }

    /**
     * 检查文件是否合法
     * 根据txIdCounter 计算理论长度和实际长度进行比较
     */
    private void checkFileIsCorruption() {
        long fileLen = 0;
        try {
            fileLen = accessFile.length();
        } catch (IOException e) {
            ForceExit.exit(BadXIDFileException);
        }

        if (fileLen < TX_ID_HEADER_LENGTH) {
            ForceExit.exit(BadXIDFileException);
        }

        // 使用堆内还是堆外需考虑
        ByteBuffer buf = ByteBuffer.allocate(TX_ID_HEADER_LENGTH);
        try {
            channel.position(0);
            channel.read(buf);
        } catch (IOException e) {
            ForceExit.exit(e);
        }
        this.txIdCounter = Parser.parseLong(buf.array());
        long endCounter = getTxIdPosition(this.txIdCounter + 1);
        if (endCounter != fileLen) {
            ForceExit.exit(BadXIDFileException);
        }
    }

    private long getTxIdPosition(long txId) {
        return TX_ID_HEADER_LENGTH + (txId - 1) * TX_ID_FIELD_SIZE;
    }

    private void updateTxId(long txId, byte state) {
        long offset = getTxIdPosition(txId);
        byte[] tmp = new byte[TX_ID_FIELD_SIZE];
        tmp[0] = state;
        ByteBuffer buf = ByteBuffer.wrap(tmp);
        try {
            channel.position(offset);
            channel.write(buf);
            channel.force(false);
        } catch (IOException e) {
            ForceExit.exit(e);
        }
    }

    private void incrTxIdCounter() {
        this.txIdCounter++;
        ByteBuffer buf = ByteBuffer.wrap(Parser.long2Byte(txIdCounter));
        try {
            channel.position(0);
            channel.write(buf);
            channel.force(false);
        } catch (IOException e) {
            ForceExit.exit(e);
        }
    }

    private boolean verifyTxId(long txId, byte state) {
        long offset = getTxIdPosition(txId);
        ByteBuffer buf = ByteBuffer.wrap(new byte[TX_ID_FIELD_SIZE]);
        try {
            channel.position(offset);
            channel.read(buf);
        } catch (IOException e) {
            ForceExit.exit(e);
        }
        return buf.array()[0] == state;
    }

    @Override
    public long begin() {
        counterLock.lock();
        try {
            long txId = txIdCounter + 1;
            updateTxId(txId, FIELD_TX_ACTIVE);
            incrTxIdCounter();
            return txId;
        } finally {
            counterLock.unlock();
        }
    }

    @Override
    public void commit(long txId) {
        updateTxId(txId, FIELD_TX_COMMITTED);
    }

    @Override
    public void abort(long txId) {
        updateTxId(txId, FIELD_TX_ABORTED);
    }

    @Override
    public boolean isActive(long txId) {
        if (txId == SUPER_TX_ID) {
            return false;
        }
        return verifyTxId(txId, FIELD_TX_ACTIVE);
    }

    @Override
    public boolean isCommitted(long txId) {
        if (txId == SUPER_TX_ID) {
            return true;
        }
        return verifyTxId(txId, FIELD_TX_COMMITTED);
    }

    @Override
    public boolean isAborted(long txId) {
        if (txId == SUPER_TX_ID) {
            return false;
        }
        return verifyTxId(txId, FIELD_TX_ABORTED);
    }

    @Override
    public void close(long txId) {
        try {
            channel.close();
            accessFile.close();
        } catch (IOException e) {
            ForceExit.exit(e);
        }
    }
}
