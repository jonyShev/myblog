# myblog

## Описание проекта:

Spring-проект, реализующий блог-платформу с возможностью добавления, редактирования, удаления постов и комментариев.

## Стек технологий:

- Java 21
- Spring Framework 6 (WebMVC, JDBC, Test)
- PostgreSQL
- Maven (war packaging)
- Tomcat / Jetty (в качестве сервлет-контейнера)
- JUnit 5
- Lombok

## Структура проекта:

- `src/main/java` — Java-код: контроллеры, сервисы, DAO, модели
- `src/main/resources` — ресурсы (HTML-шаблоны, конфиги)
- `src/test/java` — тесты

## Сборка:

```bash
mvn clean package
```

## Запуск тестов:

```bash
mvn test
```

## Деплой:

Собранный .war можно деплоить в любой сервлет-контейнер, например, Apache Tomcat.

## Как собрать и запустить:

1. Склонировать репозиторий
2. Убедиться, что установлены JDK 21, Maven и PostgreSQL
3. Выполнить сборку: mvn clean package
4. Деплой target/myblog.war в Tomcat (Пример: cp target/myblog.war /opt/homebrew/Cellar/tomcat/11.0.6/libexec/webapps/)
5. Перейти по адресу: http://localhost:8080/myblog



