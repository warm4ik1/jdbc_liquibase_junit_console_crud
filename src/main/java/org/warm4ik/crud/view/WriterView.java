package org.warm4ik.crud.view;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.controller.PostController;
import org.warm4ik.crud.controller.WriterController;
import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.model.Writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class WriterView {
    private final WriterController writerController;
    private final PostController postController;

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("1. Создать писателя");
            System.out.println("2. Получить писателя по ID");
            System.out.println("3. Получить всех писателей");
            System.out.println("4. Обновить информацию о писателе");
            System.out.println("5. Удалить писателя");
            System.out.println("0. Выйти в MainView");
            System.out.print("Выберите опцию: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createWriter();
                case 2 -> getWriterById();
                case 3 -> getAllWriters();
                case 4 -> updateWriter();
                case 5 -> deleteWriter();
                case 0 -> running = false;
                default -> System.out.println("Неверная опция. Пожалуйста, попробуйте еще раз.");
            }
        }
    }

    private void createWriter() {
        System.out.println("Введите имя: ");
        String firstname = scanner.nextLine();

        System.out.println("Введите фамилию: ");
        String lastname = scanner.nextLine();

        Writer createWriter = Writer.builder()
                .firstName(firstname)
                .lastName(lastname)
                .writerStatus(Status.ACTIVE)
                .posts(new ArrayList<>())
                .build();

        writerController.saveWriter(createWriter);
        System.out.println("Писатель создан");

        System.out.println("Хотите добавить посты для писателя? (да/нет)");
        String answer = scanner.nextLine();
        while ("да".equalsIgnoreCase(answer)) {
            Post post = createPostForWriter(createWriter);
            postController.createPost(post);

            System.out.println("Хотите добавить еще записи для писателя? (да/нет)");
            answer = scanner.nextLine();
        }
    }

    private void getWriterById() {
        System.out.println("Введите ID писателя: ");
        Integer id = scanner.nextInt();
        Writer writer = writerController.getWriterById(id);
        System.out.println("Найден писатель " + "ID:" + writer.getId() + " " + writer.getFirstName() + "_" + writer.getLastName()
                + " Status:" + writer.getWriterStatus());
    }

    private void getAllWriters() {
        List<Writer> writers = writerController.getAllWriters();
        System.out.println("Список писателей:");
        for (Writer writer : writers) {
            System.out.println("Найден писатель " + "ID:" + writer.getId() + " " + writer.getFirstName() + "_" + writer.getLastName()
                    + " Status:" + writer.getWriterStatus());
        }
    }

    private void updateWriter() {
        System.out.println("Введите ID писателя для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Введите новое имя: ");
        String firstname = scanner.nextLine();

        System.out.println("Введите новую фамилию: ");
        String lastname = scanner.nextLine();

        Writer updatedWriter = Writer.builder()
                .id(id)
                .firstName(firstname)
                .lastName(lastname)
                .writerStatus(Status.ACTIVE)
                .posts(new ArrayList<>())
                .build();

        writerController.updateWriter(updatedWriter);
        System.out.println("Писатель обновлен");
    }

    private void deleteWriter() {
        System.out.print("Введите ID писателя для удаления: ");
        Integer id = scanner.nextInt();
        writerController.deleteWriterById(id);
        System.out.println("Писатель удален.");
    }

    private Post createPostForWriter(Writer writer) {
        System.out.println("Введите содержание поста: ");
        String content = scanner.nextLine();

        return Post.builder()
                .content(content)
                .created(new Date())
                .updated(new Date())
                .postStatus(Status.ACTIVE)
                .writer(writer)
                .build();
    }
}
