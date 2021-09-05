package ru.geekbrains.rabbitmq_demo;

import com.rabbitmq.client.BuiltinExchangeType;
import ru.geekbrains.rabbitmq_demo.connections.RabbitConnection;

import java.util.Scanner;

public class SenderMain {
    private static Scanner scanner;

    public static void main(String[] args) {
        RabbitConnection connection = new RabbitConnection("myExchange", BuiltinExchangeType.DIRECT);
        while(true) {
            String message = getMessage();
            int index = message.indexOf(" ");
            if (index == -1) {
                continue;
            }
            String topic = message.substring(0, index);
            message = message.substring(index + 1);
            if (connection.sendMessage(topic, message)) {
                System.out.println("Сообщение отправлено");
            }
            else {
                System.out.println("Отправить сообщение не удалось");
            }
        }

    }

    private static String getMessage() {
        System.out.println("Введите тему и сообщение...");
        Scanner scanner = getScanner();
        return scanner.nextLine();
    }

    private static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }
}
