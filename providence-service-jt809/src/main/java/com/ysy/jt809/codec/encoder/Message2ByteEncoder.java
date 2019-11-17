package com.ysy.jt809.codec.encoder;

import com.ysy.jt809.bean.Message;
import com.ysy.jt809.util.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 编码
 *
 * @author Administrator
 */
@Slf4j
@Service
public class Message2ByteEncoder {
    public void encode(ChannelHandlerContext ctx, Message msg) {
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(PacketUtil.message2Bytes(msg));
        ctx.writeAndFlush(byteBuf).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("发送失败", future.cause());
            }
        });
    }
}
