## Как запустить:

1. Перейти в директорию `/docker` и выполнить команду для поднятия БД:
   ```
   docker compose up -d
   ```

2. Параметры подключения к БД:
    * **Host:** `localhost`
    * **Port:** `5432`
    * **Database:** `minibank`
    * **User:** `postgres`
    * **Password:** `root`
    * **URL:** `jdbc:postgresql://localhost:5432/minibank`
3. Запустить приложение:

- ```
   mvn clean compile exec:java "-Dexec.mainClass=org.pshenko.hibernate.Main"
   ```
- либо через IDE

Проверки осуществлялись вручную на чистой БД. Затем с помощью тестов из MiniBankSelfCheckTest.java
