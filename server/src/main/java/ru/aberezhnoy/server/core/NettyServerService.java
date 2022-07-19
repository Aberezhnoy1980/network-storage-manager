package ru.aberezhnoy.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.server.core.handler.CommandInboundHandler;
import ru.aberezhnoy.server.factory.Factory;
import ru.aberezhnoy.server.service.DatabaseConnectionService;
import ru.aberezhnoy.server.service.impl.ServerPropertiesReceiver;


public class NettyServerService implements ServerService {

    private static final int SERVER_PORT = ServerPropertiesReceiver.getPort();
    private static final Logger LOGGER = LogManager.getLogger(CommandInboundHandler.class);
    private static DatabaseConnectionService databaseConnectionService;

    public static DatabaseConnectionService getDatabaseConnectionService() {
        return databaseConnectionService;
    }

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel channel) {
                            channel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new CommandInboundHandler());

                        }
                    });

            ChannelFuture future = server.bind(SERVER_PORT).sync();
            LOGGER.info("Сервер запущен на порту " + SERVER_PORT);
            databaseConnectionService = Factory.getDatabaseConnectionService();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.info("Сервер упал");
            LOGGER.throwing(Level.ERROR, e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            databaseConnectionService.closeConnection();
        }
    }
}
