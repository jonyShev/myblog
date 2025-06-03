# MyBlog (Spring Boot)

## Описание проекта:

Spring Boot -проект, реализующий блог-платформу с возможностью добавления, редактирования, удаления постов и
комментариев.

## Стек технологий:

- Java 21
- Spring Boot 3.5.0
- Spring Web MVC / JDBC
- PostgreSQL (prod), H2 (tests)
- Thymeleaf
- JUnit 5 / Mockito
- Gradle
- Lombok

## Структура проекта:

- `src/main/java` — Java-код: контроллеры, сервисы, DAO, модели
- `src/main/resources` — ресурсы (HTML-шаблоны, конфиги)
- `src/test/java` — тесты

## Запуск приложения:

```bash
./gradlew bootRun
```

## Запуск тестов:

```bash
./gradlew test
```



