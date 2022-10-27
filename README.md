# java-explore-with-me
Diploma project ExploreWithMe for Yandex Practicum.

Link to pull request:
https://github.com/SergeyMazurtsev/java-explore-with-me/pull/1

# Описание запуска.
Проект состоит из двух модулей: основного сервера и сервера статистики.

Запуск осуществляется через при наличии программы Docker.

Команда для запуска:
 docker-compose up --build

В файле docker-compose.yml прописаны все необходимые переменные окружения.

# При необходимости запуска программы без docker-compose.yml на локальной компьютере, необходимо:
- необходимо запустить в терминале: docker run --name postgre -p 5555:5432 -e POSTGRES_PASSWORD=pass -d postgres:14-alpine
- в файле application.properties пакета server закомментировать строчки 13, 14, 16, 17, 18 с привязкой внешних переменных окружений. 
- в файле application.properties пакета server раскомментировать строчки 11, 12, 19, 20, 21 с явным указанием на переменные окружения.
- в файле application.properties пакета statistic закомментировать строчки 10, 11, 12 с привязкой внешних переменных окружений.
- в файле application.properties пакета statistic раскомментировать строчки 13, 14, 15 с явным указанием на переменные окружения.
- запустить mvn clean package

После чего запустить сначала ru.practicum.statistic.Statistic.java.
Потом запустить ru.practicum.ewm.ExploreWithMeApplication.java.