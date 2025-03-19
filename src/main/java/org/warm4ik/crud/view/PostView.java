package org.warm4ik.crud.view;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.controller.LabelController;
import org.warm4ik.crud.controller.PostController;
import org.warm4ik.crud.controller.WriterController;
import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.model.Writer;

import java.util.*;

@RequiredArgsConstructor
public class PostView {
    private final PostController postController;
    private final WriterController writerController;
    private final LabelController labelController;

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("1. Создать пост");
            System.out.println("2. Получить пост по ID");
            System.out.println("3. Получить все посты");
            System.out.println("4. Обновить пост");
            System.out.println("5. Удалить пост");
            System.out.println("0. Выйти в MainView");
            System.out.print("Выберите опцию: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createPost();
                case 2 -> getPostById();
                case 3 -> getAllPosts();
                case 4 -> updatePost();
                case 5 -> deletePost();
                case 0 -> running = false;
                default -> System.out.println("Неверная опция. Пожалуйста, попробуйте еще раз.");
            }
        }
    }

    private void createPost() {
        Writer writer = chooseWriter();

        if (writer != null) {
            System.out.println("Введите содержание поста: ");
            String content = scanner.nextLine();

            List<Label> labels = updateLabelsForPost(new Post());

            Post post = Post.builder()
                    .content(content)
                    .created(new Date())
                    .updated(new Date())
                    .postStatus(Status.ACTIVE)
                    .writer(writer)
                    .labels(labels)
                    .build();

            postController.createPost(post);
            System.out.println("Пост сохранен.");
        }
    }

    private void getPostById() {
        System.out.print("Введите ID поста: ");
        Integer postId = scanner.nextInt();
        scanner.nextLine();

        Post post = postController.getPostById(postId);
        System.out.print("Найден пост: ");
        Writer writer = post.getWriter();
        String writerInfo = "Writer{id=" + writer.getId() + ", firstName='" + writer.getFirstName() +
                "', lastName='" + writer.getLastName() + "'}";
        System.out.println("Post{id=" + post.getId() + ", content='" + post.getContent() +
                "', post_status='" + post.getPostStatus() + "', post_created='"
                + post.getCreated() + "', post_updated='" + post.getUpdated()
                + "', post_labels='" + post.getLabels() + "', " + writerInfo + "}");
    }

    private void getAllPosts() {
        List<Post> posts = postController.getAllPosts();
        for (Post post : posts) {
            Writer writer = post.getWriter();
            String writerInfo = "Writer{id=" + writer.getId() + ", firstName='" + writer.getFirstName() +
                    "', lastName='" + writer.getLastName() + "'}";
            System.out.println("Post{id=" + post.getId() + ", content='" + post.getContent() +
                    "', post_status='" + post.getPostStatus() + "', post_created='"
                    + post.getCreated() + "', post_updated='" + post.getUpdated()
                    + "', post_labels='" + post.getLabels() + "', " + writerInfo + "}");
        }
    }

    private void updatePost() {
        System.out.print("Введите ID поста для обновления: ");
        Integer postId = Integer.parseInt(scanner.nextLine());

        Post existingPost = postController.getPostById(postId);
        if (existingPost == null) {
            System.out.println("Пост не найден.");
            return;
        }

        System.out.println("Введите обновленное содержание поста: ");
        String content = scanner.nextLine();

        List<Label> updatedLabels = updateLabelsForPost(existingPost);

        existingPost.setContent(content);
        existingPost.setUpdated(new Date());
        existingPost.setPostStatus(Status.ACTIVE);
        existingPost.setLabels(updatedLabels);

        postController.updatePost(existingPost);
        System.out.println("Пост обновлен.");
    }


    private void deletePost() {
        System.out.println(postController.getAllPosts());
        System.out.print("Введите ID поста для удаления: ");
        Integer postId = scanner.nextInt();
        scanner.nextLine();

        postController.deletePost(postId);
        System.out.println("Пост удален.");
    }

    private Writer chooseWriter() {
        System.out.println("Список писателей:");
        List<Writer> writers = writerController.getAllWriters();
        for (Writer w : writers) {
            System.out.println("id:" + w.getId() + " ||| " + "name:" + w.getFirstName() + " ||| " + "last_name:"
                    + w.getLastName() + " |||" + "status:" + w.getWriterStatus() + " ||| ");
        }
        System.out.println("Выберите писателя для поста по ID: ");
        Integer writerId = scanner.nextInt();
        scanner.nextLine();

        Writer writer = writerController.getWriterById(writerId);
        if (writer != null) {
            System.out.println("Выбранный писатель: " + writer.getFirstName() + " " + writer.getLastName());
        }
        return writer;
    }

    private List<Label> updateLabelsForPost(Post post) {
        List<Label> updatedLabels = post.getLabels() == null ? new ArrayList<>() : new ArrayList<>(post.getLabels());

        System.out.println("Текущие метки поста:");
        updatedLabels.forEach(label -> System.out.println(label.getId() + ". " + label.getName()));

        // Удаление меток
        boolean removeMoreLabels = true;
        while (removeMoreLabels) {
            System.out.println("Хотите удалить метку? (да/нет)");
            if ("да".equalsIgnoreCase(scanner.nextLine())) {
                System.out.println("Введите ID метки:");
                Integer labelIdToRemove = Integer.parseInt(scanner.nextLine());

                Label labelToRemove = updatedLabels.stream()
                        .filter(label -> label.getId().equals(labelIdToRemove))
                        .findFirst()
                        .orElse(null);

                if (labelToRemove != null) {
                    updatedLabels.remove(labelToRemove);
                    labelController.removeLabelFromPost(post.getId(), labelIdToRemove);
                    System.out.println("Метка удалена.");
                } else {
                    System.out.println("Метка не найдена.");
                }
            } else {
                removeMoreLabels = false;
            }
        }

        // Добавление новых меток
        boolean addLabels = true;
        while (addLabels) {
            System.out.println("Хотите добавить метку? (да/нет)");
            if ("да".equalsIgnoreCase(scanner.nextLine())) {
                System.out.println("Введите название метки: ");
                String labelName = scanner.nextLine();

                Label newLabel = Label.builder()
                        .name(labelName)
                        .labelStatus(Status.ACTIVE)
                        .build();

                labelController.createLabel(newLabel);
                updatedLabels.add(newLabel);
                System.out.println("Метка добавлена.");
            } else {
                addLabels = false;
            }
        }
        return updatedLabels;
    }
}
