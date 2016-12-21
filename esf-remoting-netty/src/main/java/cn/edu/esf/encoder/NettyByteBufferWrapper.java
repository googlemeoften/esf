package cn.edu.esf.encoder;

import cn.edu.esf.BaseResponse;
import cn.edu.esf.protocol.ByteBufferWrapper;
import io.netty.buffer.ByteBuf;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class NettyByteBufferWrapper implements ByteBufferWrapper {
    private ByteBuf buffer;

    public NettyByteBufferWrapper(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public ByteBufferWrapper writeByte(int index, byte data) {
        buffer.writeByte(data);
        return this;
    }

    @Override
    public ByteBufferWrapper writeByte(byte data) {
        buffer.writeByte(data);
        return this;
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public ByteBufferWrapper writeInt(int data) {
        buffer.writeInt(data);
        return this;
    }

    @Override
    public ByteBufferWrapper writeBytes(byte[] data) {
        buffer.writeBytes(data);
        return this;
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public void readBytes(byte[] dst) {
        buffer.readBytes(dst);
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }

    @Override
    public void setReaderIndex(int readerIndex) {
        buffer.setIndex(readerIndex,buffer.writerIndex());
    }

    @Override
    public ByteBufferWrapper writeLong(long id) {
        buffer.writeLong(id);
        return this;
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public void send(BaseResponse object) {

    }

    @Override
    public void ensureCapacity(int capacity) {
        buffer.capacity(capacity);
    }
}
