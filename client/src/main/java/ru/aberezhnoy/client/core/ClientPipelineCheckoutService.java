package ru.aberezhnoy.client.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.aberezhnoy.client.core.handler.ClientInboundCommandHandler;
import ru.aberezhnoy.client.core.handler.FilesInboundClientHandler;
import ru.aberezhnoy.client.service.Callback;

public class ClientPipelineCheckoutService {

    public static void createPipelineForFilesSending (ChannelHandlerContext ctx) {
        ctx.pipeline().addLast(new ChunkedWriteHandler());
        ctx.pipeline().remove(ObjectEncoder.class);
    }

    public static void createBasePipelineAfterUploadForInOutCommandTraffic(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(ChunkedWriteHandler.class);
        ctx.pipeline().remove(ObjectDecoder.class);
        ctx.pipeline().addFirst(new ObjectEncoder());
        ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
    }

    public static void createPipelineForInboundFilesReceiving(ChannelHandlerContext ctx, String fileName, String userDirectory, Long fileSize, Callback setButtonsAbleAndUpdateFilesListCallback) {
        ctx.pipeline().remove(ClientInboundCommandHandler.class);
        ctx.pipeline().remove(ObjectDecoder.class);
        ctx.pipeline().addLast(new ChunkedWriteHandler());
        ctx.pipeline().addLast(new FilesInboundClientHandler(fileName, userDirectory, fileSize, setButtonsAbleAndUpdateFilesListCallback));
    }

    public static void createBasePipelineAfterDownloadForInOutCommandTraffic(ChannelHandlerContext ctx, Callback callback) {
        ctx.pipeline().remove(ChunkedWriteHandler.class);
        ctx.pipeline().addFirst(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        ctx.pipeline().addLast(new ClientInboundCommandHandler(callback));
        ctx.pipeline().remove(FilesInboundClientHandler.class);
    }
}
