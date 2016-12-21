package cn.edu.esf.protocol;

import cn.edu.esf.BaseResponse;

/**
 * 依赖底层NIO的实现，包装器; 不依赖具体NIO的关键
 * 
 * @author kongming.lrq
 */
public interface ByteBufferWrapper {

    public ByteBufferWrapper writeByte(int index, byte data);

    public ByteBufferWrapper writeByte(byte data);

    public byte readByte();

    public ByteBufferWrapper writeInt(int data);

    public ByteBufferWrapper writeBytes(byte[] data);

    public int readableBytes();

    public int readInt();

    public void readBytes(byte[] dst);

    public int readerIndex();

    public void setReaderIndex(int readerIndex);

    public ByteBufferWrapper writeLong(long id);

    public long readLong();

    public void send(BaseResponse object);

    public void ensureCapacity(int capacity);

}
