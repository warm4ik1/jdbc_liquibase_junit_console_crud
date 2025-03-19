package org.warm4ik.crud.view;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.ApplicationContext;

import java.util.Scanner;

@RequiredArgsConstructor
public class MainView {
    private final ApplicationContext applicationContext;
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("Выберите опцию:");
            System.out.println("1. Писатель");
            System.out.println("2. Пост");
            System.out.println("3. Метка");
            System.out.println("0. Завершить программу.");
            System.out.print("Введите число: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> applicationContext.writerRun();
                case 2 -> applicationContext.postRun();
                case 3 -> applicationContext.labelRun();
                case 0 -> {
                    System.out.println("Завершение работы...");
                    running = false;
                }
                default -> System.out.println("Неверный выбор. Попробуйте ещё раз.");
            }
        }
    }
}
