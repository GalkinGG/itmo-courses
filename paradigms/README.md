# Решения заданий курса "Парадигмы программирования"
## Java <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original.svg" title="java" alt="java" width="50" height="50" />
### Домашнее задание 1. Обработка ошибок
Продолжение [домашнего задания №13](/prog-intro/java-solutions/expression/parser/) c курса prog-intro. Парсер выражений с обработкой ошибок разбора и вычислений.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#except)  
[Решение](/paradigms/java-solutions/expression/)
### Домашнее задание 2. Бинарный поиск
Реализация итеративного и рекурсивного вариантов бинарного поиска в массиве. Для реализаций методов приведены доказательства соблюдения контрактов в терминах [троек Хоара](https://ru.wikipedia.org/wiki/%D0%9B%D0%BE%D0%B3%D0%B8%D0%BA%D0%B0_%D0%A5%D0%BE%D0%B0%D1%80%D0%B0).  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#java-binary-search)  
[Решение](/paradigms/java-solutions/search/)  
### Домашнее задание 3. Очередь на массиве
Реализация классов, представляющих циклическую очередь на основе массива. Модель, инвариант, пред- и постусловия записаны в исходном коде в виде комментариев.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#java-array-queue)  
[Решение](/paradigms/java-solutions/queue/)  
### Домашнее задание 4. Очереди
Реализация очереди на связном списке. Выделение общих частей классов очередей в базовый класс AbstractQueue.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#java-queues)  
[Решение](/paradigms/java-solutions/queue/)
### Домашнее задание 5. Вычисление в различных типах
Добавление в программу разбирающую и вычисляющую выражения трех переменных поддержку вычисления в различных типах.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#java-tabulator)  
[Решение](/paradigms/java-solutions/expression/)
## JavaScript <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/javascript/javascript-original.svg" title="js" alt="js" width="50" height="50" />
### Домашнее задание 6. Функциональные выражения на JavaScript
Реализацмя функций cnst, variable, add, subtract, multiply, divide, negate для вычисления выражений с тремя переменными: x, y и z.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#js-functional-expressions)   
[Решение](/paradigms/javascript-solutions/functionalExpression.js)
### Домашнее задание 7. Объектные выражения на JavaScript
Реализация классов Const, Variable, Add, Subtract, Multiply, Divide, Negate для представления выражений с тремя переменными: x, y и z.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#js-object-expressions)   
[Решение](/paradigms/javascript-solutions/objectExpression.js)
### Домашнее задание 8. Обработка ошибок на JavaScript
Добавление в предыдущее домашнее задание функции parsePrefix(string), разбирающей выражения,   
задаваемые записью вида «(- (* 2 x) 3)». Если разбираемое выражение некорректно, метод parsePrefix бросает ошибки с человеко-читаемыми сообщениями.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#js-expression-parsing)   
[Решение](/paradigms/javascript-solutions/objectExpression.js)
## Clojure <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/clojure/clojure-original.svg" title="clojure" alt="clojure" width="50" height="50"/>
### Домашнее задание 9. Линейная алгебра на Clojure
Реализация функций для работы с числами, векторами и матрицами.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#clojure-linear)   
[Решение](/paradigms/clojure-solutions/linear.clj)
### Домашнее задание 10. Функциональные выражения на Clojure
Реализация функций constant, variable, add, subtract, multiply, divide и negate для представления арифметических выражений, а также разборщика выражений в стандартной для Clojure форме.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#clojure-functional-expressions)   
[Решение](/paradigms/clojure-solutions/expression.clj)
### Домашнее задание 11. Объектные выражения на Clojure
Реализация конструкторов Constant, Variable, Add, Subtract, Multiply, Divide и Negate для представления арифметических выражений, а также разборщика выражений в стандартной для Clojure форме.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#clojure-object-expressions)   
[Решение](/paradigms/clojure-solutions/expression.clj)
### Домашнее задание 12. Комбинаторные парсеры
Реализация функции (parseObjectPostfix "expression"), разбирающей выражения, записанные в постфиксной форме, и функции toStringPostfix, возвращающей строковое представление выражения в этой форме. Функции разбора базируются на библиотеке комбинаторов, разработанной на лекции.  
[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#clojure-expression-parsing)   
[Решение](/paradigms/clojure-solutions/expression.clj)
## Prolog <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/prolog/prolog-original.svg" title="prolog" alt="prolog" width="50" height="50"/>
### Домашнее задание 13. Простые числа на Prolog
Реализация правил:
* prime(N), проверяющее, что N – простое число.
* composite(N), проверяющее, что N – составное число.
* prime_divisors(N, Divisors), проверяющее, что список Divisors содержит все простые делители числа N, упорядоченные по возрастанию.  

[Текст задания](https://www.kgeorgiy.info//courses/paradigms/homeworks.html#prolog-primes)   
[Решение](/paradigms/prolog-solutions/primes.pl)
