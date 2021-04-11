package ru.geekbains.sklyarov.network.chat.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;


public class ServerApp {
    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    public static void main(String[] args) {
        try {
            new Server(9000);
        } catch (UnknownHostException e) {
            logger.throwing(Level.FATAL, e);
            throw new RuntimeException("Unknown host");

        }
    }
}
