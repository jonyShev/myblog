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
2. Убедиться, что установлен JDK 21 и Maven
3. Выполнить mvn clean package
4. Деплоить target/myblog.war в Tomcat



