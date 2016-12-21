package cn.edu.esf.encoder;

import cn.edu.esf.protocol.ByteBufferWrapper;
import cn.edu.esf.protocol.Protocol;
import cn.edu.esf.protocol.ProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class NettyProtocolDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyProtocolDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        NettyByteBufferWrapper wrapper = new NettyByteBufferWrapper(in);
        Object msg = this.decode(wrapper);
        if (msg != null) {
            out.add(msg);
        }
    }

    private Object decode(ByteBufferWrapper wrapper) throws Exception {
        final int originPos = wrapper.readerIndex();
        if (wrapper.readableBytes() < 1) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        byte type = wrapper.readByte();
        Protocol protocol = ProtocolFactory.instance.getProtocol(type);
        if (protocol == null) {
            LOGGER.warn("Unsupport protocol type: " + type);
            return null;
        }
        return protocol.decode(wrapper, originPos);
    }

}