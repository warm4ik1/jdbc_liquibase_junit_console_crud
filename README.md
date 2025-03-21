Стэк: Java, PostgreSQL, JDBC, Maven, Liquibase, JUnit, Mockito.

Консольное CRUD приложение, которое взаимодействует с БД и позволяет выполнять все CRUD операции над сущностями.

Writer (id, firstName, lastName, List<Post> posts)
Post (id, content, created, updated, List<Label> labels)
Label (id, name)
Status (enum ACTIVE, UNDER_REVIEW, DELETED)

Каждая сущность имеет поле Status. В момент удаления, мы не удаляем запись из бд, а меняем её статус на DELETED.

Миграция бд через liquibase(xml).