package ru.aberezhnoy.client.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.client.core.ClientPipelineCheckoutService;
import ru.aberezhnoy.client.factory.Factory;
import ru.aberezhnoy.client.service.Callback;
import ru.aberezhnoy.client.service.CommandDictionaryService;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;

import java.util.Arrays;

public class ClientInboundCommandHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger LOGGER = LogManager.getLogger(ClientInboundCommandHandler.class);
    private final CommandDictionaryService commandDictionary;
    private final Callback setButtonsAbleAndUpdateFilesListCallback;

    public ClientInboundCommandHandler(Callback setButtonsAbleAndUpdateFilesListCallback) {
        this.setButtonsAbleAndUpdateFilesListCallback = setButtonsAbleAndUpdateFilesListCallback;
        this.commandDictionary = Factory.getCommandDictionary();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) {
        if (command.getCommandName().startsWith(CommandType.READY_TO_UPLOAD.toString())) {
            LOGGER.info("Получена с сервера команда READY_TO_UPLOAD со списком аргументов: " + Arrays.asList(command.getArgs()));

            ClientPipelineCheckoutService.createPipelineForFilesSending(ctx);

            commandDictionary.processCommand(command);

        } else if (command.getCommandName().startsWith(CommandType.UPLOAD_FINISHED.toString())) {
            LOGGER.info("Получена с сервера команда UPLOAD_FINISHED для файла " + Arrays.asList(command.getArgs()));

            ClientPipelineCheckoutService.createBasePipelineAfterUploadForInOutCommandTraffic(ctx);

            sendFilesListCommand(ctx, command);

            setButtonsAble();
        } else if (command.getCommandName().startsWith(CommandType.READY_TO_DOWNLOAD.toString())) {
            LOGGER.info("Получена с сервера команда READY_TO_DOWNLOAD с аргументами " + Arrays.asList(command.getArgs()));

            ClientPipelineCheckoutService.createPipelineForInboundFilesReceiving(ctx, (String) command.getArgs()[0],
                    (String) command.getArgs()[3], (Long) command.getArgs()[2], setButtonsAbleAndUpdateFilesListCallback);

            sendReadyToReceiveCommand(ctx, command);

        } else {
            commandDictionary.processCommand(command);
        }
    }

    private void sendFilesListCommand(ChannelHandlerContext ctx, Command command) {
        String[] args = {(String) command.getArgs()[1]};
        ctx.writeAndFlush(new Command(CommandType.FILES_LIST.toString(), args));
        LOGGER.info("На сервер отправлена команда FILES_LIST с аргументами " + Arrays.toString(args));
    }

    private void setButtonsAble() {
        if (setButtonsAbleAndUpdateFilesListCallback != null) {
            setButtonsAbleAndUpdateFilesListCallback.callback();
        }
    }

    private void sendReadyToReceiveCommand(ChannelHandlerContext ctx, Command command) {
        Object[] argsToServer = {command.getArgs()[0], command.getArgs()[1]};
        ctx.writeAndFlush(new Command(CommandType.READY_TO_RECEIVE.toString(), argsToServer));
        LOGGER.info("На сервер отправлена команда READY_TO_RECEIVE с аргументами " + Arrays.asList(argsToServer));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.throwing(Level.ERROR, cause);
    }
}
