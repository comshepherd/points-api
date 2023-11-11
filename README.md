# Генерация JOOQ
Чтобы сгенерировать классы нужно запустить gradle task 

**gradle generateJooqClasses**

Eсли у вас Mac OS и Docker Desktop выше версии 4.13, то вы можете столкнуться с ошибкой, что не найден файл сокета докера.

### Что нужно сделать
1. Откройте терминал в корне проекта
2. Установите значение переменной окружения 

**export DOCKER_HOST=unix:///Users/ИМЯ_ПОЛЬЗОВАТЕЛЯ/.docker/run/docker.sock**

2. Запустите **./gradlew generateJooqClasses**

Это связано с тем, что Docker Desktop в новых версиях перенес файл (символическую ссылку) сокета docker demon из общего /var в пользовательскую директорию 