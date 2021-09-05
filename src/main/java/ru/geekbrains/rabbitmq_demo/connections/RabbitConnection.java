package ru.geekbrains.rabbitmq_demo.connections;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitConnection {
    private ConnectionFactory factory;
    private String exchangeName;
    private BuiltinExchangeType type;
    private Channel channel;

    public RabbitConnection(String exchangeName, BuiltinExchangeType type) {
        this.exchangeName = exchangeName;
        this.type = type;
    }

    public Connection getConnection() {
        try {
            return getFactory().newConnection();
        }
        catch (IOException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ConnectionFactory getFactory() {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);//Тут могла бы быть настройка подключения
        }
        return factory;
    }

    public boolean sendMessage(String topicName, String message) {
        try(Connection conn = getConnection()) {
            if (conn == null) {
                return false;
            }
            Channel channel = getChannel(conn);
            channel.basicPublish(exchangeName, topicName, null, message.getBytes(StandardCharsets.UTF_8));
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Channel getChannel(Connection conn) throws IOException {
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(exchangeName, type);
        return channel;
    }

    public boolean listenMessages(String topicName, DeliverCallback callback) {
        Connection conn = getConnection();
        if (conn == null) {
            return false;
        }
        try {
            Channel channel = getChannel(conn);
            this.channel = channel;
            String queue = channel.queueDeclare().getQueue();
            channel.queueBind(queue, exchangeName, topicName);
            channel.basicConsume(queue, true, callback, consumerTag -> { });
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                conn.close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
    }

    public void closeChannel() {
        if (this.channel == null) {
            return;
        }
        try {
            this.channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
