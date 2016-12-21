package cn.edu.esf.encoder;

import cn.edu.esf.BaseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class NettyProtocolEncoder extends MessageToByteEncoder<Object> {

    public NettyProtocolEncoder() {
        super(false);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        NettyByteBufferWrapper byteBufferWrapper = new NettyByteBufferWrapper(out);
        ((BaseHeader) message).encode(byteBufferWrapper);
    }

}