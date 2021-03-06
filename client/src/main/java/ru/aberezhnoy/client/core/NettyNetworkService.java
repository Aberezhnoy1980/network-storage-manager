package ru.aberezhnoy.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedFile;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.client.core.handler.ClientInboundCommandHandler;
import ru.aberezhnoy.client.service.Callback;
import ru.aberezhnoy.client.service.impl.ClientPropertiesReceiver;
import ru.aberezhnoy.common.domain.Command;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class NettyNetworkService implements NetworkService {

    private static final String SERVER_HOST = ClientPropertiesReceiver.getHost();
    private static final int SERVER_PORT = ClientPropertiesReceiver.getPort();
    private static final Logger LOGGER = LogManager.getLogger(NettyNetworkService.class);
    public static SocketChannel channel;

    private NettyNetworkService() {

    }

    public static NettyNetworkService initializeNetwork(Callback setButtonsAbleAndUpdateFilesListCallback) {
        NettyNetworkService network = new NettyNetworkService();
        initializeNetworkService(setButtonsAbleAndUpdateFilesListCallback);
        return network;
    }

    public static void initializeNetworkService(Callback setButtonsAbleAndUpdateFilesListCallback) {
        Thread t = new Thread(() -> {
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel socketChannel) {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast (
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new ClientInboundCommandHandler(setButtonsAbleAndUpdateFilesListCallback)
                                        );
                            }
                        });
                ChannelFuture future = bootstrap.connect(SERVER_HOST, SERVER_PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                LOGGER.throwing(Level.ERROR, e);
            } finally {
                workGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void sendCommand(Command command) {
        channel.writeAndFlush(command);
        LOGGER.info("?? ?????????????? ???? ???????????? ???????????????????? ?????????????? " + command.getCommandName() + " ?? ?????????????????????? " + Arrays.asList(command.getArgs()));
    }

    @Override
    public void sendFile(String pathToFile) {
        try {
            ChannelFuture future = channel.writeAndFlush(new ChunkedFile(new File(pathToFile)));
            LOGGER.info("???????????????? ???????????????? ?????????? ???? ???????????? ???? ???????? " + pathToFile);
            future.addListener((ChannelFutureListener) channelFuture -> LOGGER.info("???????? ??????????????"));
        } catch (IOException e) {
            LOGGER.throwing(Level.ERROR, e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (isConnected()) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            LOGGER.throwing(Level.ERROR, e);
        }
    }

    @Override
    public boolean isConnected() {
        return channel != null && !channel.isShutdown();
    }

}
