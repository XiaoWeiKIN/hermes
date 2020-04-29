package com.bolt.common.buffer;


import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/6
 * @Description: TODO
 */
public class ChannelBufferInputStream extends InputStream {
    private final ChannelBuffer buffer;
    private final int startIndex;
    private final int endIndex;

    public ChannelBufferInputStream(ChannelBuffer buffer, int length) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length > buffer.readableBytes()) {
            throw new IndexOutOfBoundsException();
        }

        this.buffer = buffer;
        startIndex = buffer.readerIndex();
        endIndex = startIndex + length;
        buffer.markReaderIndex();
    }

    /**
     * 返回这个流中已经读取的字节数
     * @return
     */
    public int readBytes() {
        return buffer.readerIndex() - startIndex;
    }

    /**
     * 返回这个流中的可读字节数
     *
     * @return
     * @throws IOException
     */
    @Override
    public int available() throws IOException {
        return endIndex - buffer.readerIndex();
    }

    /**
     * 返回下一个可读字节
     *
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (!buffer.readable()) {
            return -1;
        }
        return buffer.readByte() & 0xff;
    }

    /**
     * 从输入流读取最多 len字节的数据到一个字节数组。
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int available = available();
        if (available == 0) {
            return -1;
        }

        len = Math.min(available, len);
        buffer.readBytes(b, off, len);
        return len;
    }

    /**
     * 重置
     * @throws IOException
     */
    @Override
    public void reset() throws IOException {
        buffer.resetReaderIndex();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * 跳过并丢弃来自此输入流的 n字节数据。
     * @param n
     * @return
     * @throws IOException
     */
    @Override
    public long skip(long n) throws IOException {
        if (n > Integer.MAX_VALUE) {
            return skipBytes(Integer.MAX_VALUE);
        } else {
            return skipBytes((int) n);
        }
    }

    private int skipBytes(int n) throws IOException {
        int nBytes = Math.min(available(), n);
        buffer.skipBytes(nBytes);
        return nBytes;
    }
}
