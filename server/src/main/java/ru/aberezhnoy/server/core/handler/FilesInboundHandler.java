package ru.aberezhnoy.server.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;
import ru.aberezhnoy.server.core.ServerPipelineCheckoutService;

import java.io.*;
import java.util.Arrays;

public class FilesInboundHandler extends ChannelInboundHandlerAdapter {

    private final String fileName;
    private final String userDirectory;
    private final String login;
    private final Long fileSize;

    public FilesInboundHandler(String fileName, String userDirectory, String login, Long fileSize) {
        this.fileName = fileName;
        this.userDirectory = userDirectory;
        this.login = login;
        this.fileSize = fileSize;
    }

    private static final Logger LOGGER = LogManager.getLogger(CommandInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) throws Exception {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;

        String absoluteFileNameForCloud = userDirectory + File.separator + fileName;
        File newFile = new File(absoluteFileNameForCloud);
        newFile.createNewFile();

        LOGGER.info("Создан файл и запущен процесс приема файла на сервере по пути " + absoluteFileNameForCloud);

        writeNewFileContent(absoluteFileNameForCloud, byteBuf);

        createAnswerAboutSuccessUpload(newFile, ctx);
    }

    private void writeNewFileContent(String absoluteFileNameForCloud, ByteBuf byteBuf) throws IOException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(absoluteFileNameForCloud, true))) {
            while (byteBuf.isReadable()) {
                out.write(byteBuf.readByte());
            }
            byteBuf.release();
        }
    }

    private void createAnswerAboutSuccessUpload(File file, ChannelHandlerContext ctx) {
        if (file.length() == fileSize) {
            LOGGER.info("Файл вычитан");
            ServerPipelineCheckoutService.createBasePipelineAfterUploadForInOutCommandTraffic(ctx);

            String[] args = {fileName, login};
            ctx.writeAndFlush(new Command(CommandType.UPLOAD_FINISHED.toString(), args));
            LOGGER.info("На клиент с сервера отправлена команда UPLOAD_FINISHED с аргументами " + Arrays.toString(args));

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.throwing(Level.ERROR, cause);
    }

}
