# Методы нулевого порядка

## Основное задание
Реализуйте и исследуйте на эффективность следующие методы:
1. метод градиентного спуска с постоянным шагом (learning rate);
2. любой метод одномерного поиска и градиентный спуск на его основе;
3. метод Нелдера-Мида. При этом используйте готовую реализацию в
Python библиотеке scipy.optimize. Изучите возможности библиотеки
scipy.optimize.  

## Содержание исследования. 
Для исследования подберите 2-3 квадратичные
функции двух переменных, на которых эффективность методов будет явно
отличаться; Сравните методы на каждой из этих функциях:
1. исследуйте сходимость и сравните эффективность методов на выбранных
функциях, с учетом количества итераций и количества вычислений
значений минимизируемой функции и ее градиентов, в зависимости от
желаемой точности;
2. исследуйте работу методов в зависимости от выбора начальной точки;
3. в каждом случае иллюстрируйте примеры. Нарисуйте графики
рассматриваемых функций (3D), нарисуйте графики с линиями уровня и
траекториями методов (2D, в области задания). Вычисленные значения
оформите в виде сравнительных таблиц.

## Дополнительное задание 1 (на выбор один из пунктов)
Реализуйте и исследуйте на эффективность следующие методы:
1. метод покоординатного спуска;
2. еще один метод одномерного поиска и градиентный спуск на его основе.
(можно взять методы, как обсуждаемы на лекциях, так и другие.)
3. Сделайте свою реализацию метода Нелдера-Мида или разберите какую-
либо стороннюю, библиотечную реализацию данного метода,
проанализируйте особенности реализации, модифицируйте и
интегрируйте код метода в свой проект.

## Дополнительное задание 2
Проведите исследование сложных случаев:
1. исследуйте эффективность методов на функциях n переменных, в
зависимости от размерности пространства n;
2. исследуйте эффективность методов на плохо обусловленных функциях
двух переменных;
3. исследуйте эффективность методов на функциях с зашумленными
значениями и на мультимодальных функциях.