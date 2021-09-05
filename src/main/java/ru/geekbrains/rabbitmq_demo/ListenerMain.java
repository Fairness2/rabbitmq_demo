package ru.geekbrains.rabbitmq_demo;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.DeliverCallback;
import ru.geekbrains.rabbitmq_demo.connections.RabbitConnection;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ListenerMain {
    private static Scanner scanner;

    public static void main(String[] args) {
        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.printf("Получено: %s%n", message);
        };
        RabbitConnection connection = new RabbitConnection("myExchange", BuiltinExchangeType.DIRECT);
        while(true) {
            String topic = getTopic();
            connection.closeChannel();
            if (connection.listenMessages(topic, callback)) {
                System.out.printf("Прослушивание темы %s%n", topic);
            }
            else {
                System.out.println("Установить тему не удалось");
            }
        }
    }

    private static String getTopic() {
        System.out.println("Введите тему...");
        Scanner scanner = getScanner();
        return scanner.next();
    }

    private static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }
}
