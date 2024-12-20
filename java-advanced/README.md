# Решения заданий курса "Технологии Java"
### Домашнее задание 1. Обход файлов
Реализация класса Walk, осуществляющего подсчет хэш-сумм файлов
с помощью [алгоритма Дженкинса](https://en.wikipedia.org/wiki/Jenkins_hash_function). Входной файл
содержит список файлов, которые требуется обойти. Выходной файл содержит по
одной строке формата **<шестнадцатеричная хэш-сумма> <путь к файлу>** для каждого файла.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#walk)   
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/walk)
### Домашнее задание 2. Множество на массиве
Класс ArraySet, реализующий неизменяемое упорядоченное множество.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#arrayset)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/arrayset)
### Домашнее задание 3. Студенты
Реализация класса StudentDB, осуществляющего поиск по базе данных студентов.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#student)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/student)
### Домашнее задание 4. Implementor
Реализация класса Implementor, генерирующего реализации интерфейсов.
Аргумент командной строки: полное имя интерфейса, для которого требуется сгенерировать реализацию. Методы сгенерированного класса игнорируют свои аргументы и возвращают значения по умолчанию.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#implementor)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/implementor)
### Домашнее задание 5. Jar Implementor
Расширение класса Implementor для работы с jar-файлами.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#implementor-jar)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/implementor)
### Домашнее задание 6. Javadoc
Документирование класса Implementor с предоставлением скрипта для генерации документации.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#implementor-javadoc)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/implementor)
### Домашнее задание 7. Итеративный параллелизм
Реализация класса IterativeParallelism, который обрабатывает списки в несколько потоков
без использования *Concurrency Utilities* и *Parallel Streams*.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#concurrent)   
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/iterative)
### Домашнее задание 8. Параллельный запуск
Класс ParallelMapperImpl, реализующий интерфейс ParallelMapper.
Метод параллельно вычисляет функцию f на каждом из указанных аргументов (List<> args).
Метод close останавливает все рабочие потоки.
Также доработан класс IterativeParallelism так, чтобы он мог использовать ParallelMapper.
При выполнении задания всё ещё не используются *Concurrency Utilities* и *Parallel Streams*.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#mapper)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/iterative)
### Домашнее задание 9. Web Crawler
Реализация потокобезопасного класса WebCrawler, который рекурсивно обходит сайты.
Задание подразумевает активное использование *Concurrency Utilities*.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#crawler)  
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/crawler)
### Домашнее задание 10. HelloUDP
Реализация клиента и сервера, взаимодействующих по UDP.
Класс **HelloUDPClient** отправляет запросы на сервер, принимает результаты и выводит их на консоль.
Класс **HelloUDPServer** принимает задания, отсылаемые классом HelloUDPClient и отвечает на них.  
[Текст задания](https://www.kgeorgiy.info//courses/java-advanced/homeworks.html#hello-udp)   
[Решение](/java-advanced/java-solutions/info/kgeorgiy/ja/galkin/hello)