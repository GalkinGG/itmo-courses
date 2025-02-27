import matplotlib.pyplot as plt
import numpy as np
from scipy import optimize
from sympy import *
import random


phi = (1 + 5 ** 0.5) / 2


# Вычисление значения функции
def fun(f, point):
    return f.subs([(symbols(f'x{i}'), point[i]) for i in range(len(point))]).evalf(15)


# Вычисление градиента в точке
def gradient(f, point):
    vars = symbols(f'x0:{len(point)}')
    grad = [diff(f, var).subs([(vars[i], point[i]) for i in range(len(point))]).evalf(15) for var in vars]
    return grad


# Условие останова
def stop_condition(point, prev, eps):
    return np.linalg.norm(np.array(point, dtype=float) - np.array(prev, dtype=float)) < eps


# Градиентный спуск с фиксированным шагом
def gradient_descent(f, start_point, learning_rate=1e-2, eps=1e-5, iters=1e7):
    points = [start_point]
    counter = 0
    while counter < iters:
        grad = gradient(f, points[-1])
        new_point = [points[-1][i] - learning_rate * grad[i] for i in range(len(points[-1]))]
        if stop_condition(new_point, points[-1], eps):
            break
        points.append(new_point)
        counter += 1
    return counter, points[-1], fun(f, points[-1]), points, 0, counter


# Метод дихотомии
def dichotomy(f, point, grad, eps=1e-5):
    func_counter = 0
    l = eps
    r = 100
    delta = eps / 2

    def step(rate):
        return [point[i] - rate * grad[i] for i in range(len(point))]

    while not r - l < eps:
        x1 = (r + l - delta) / 2
        x2 = (r + l + delta) / 2
        if fun(f, step(x1)) < fun(f, step(x2)):
            r = x2
        else:
            l = x1
        func_counter += 2

    return (r + l) / 2, func_counter


# Градиентный спуск на основе дихотомии
def dichotomy_descent(f, start_point, eps=1e-5):
    func_counter = 0
    grad_counter = 0
    points = [start_point]
    counter = 0
    while True:
        counter += 1
        grad = gradient(f, points[-1])
        grad_counter += 1
        res = dichotomy(f, points[-1], grad, eps)
        learning_rate = res[0]
        func_counter += res[1]
        new_point = [points[-1][i] - learning_rate * grad[i] for i in range(len(points[-1]))]
        if stop_condition(new_point, points[-1], eps):
            break
        points.append(new_point)
    return counter, points[-1], fun(f, points[-1]), points, func_counter, grad_counter


# Метод Нелдера-Мида
def nelder_mead(f, x0, eps):
    res = optimize.minimize(f, x0, method="Nelder-Mead", options={"xatol": eps, "return_all": True})
    return res["nit"], res["x"], res["fun"], res["allvecs"], res["nfev"], 0


# Метод золотого сечения
def golden_ratio(f, point, grad, eps=1e-5):
    func_counter = 0
    l = eps
    r = 100

    def step(rate):
        return [point[i] - rate * grad[i] for i in range(len(point))]

    while not r - l < eps:
        delta = (r - l) / phi
        x1 = r - delta
        x2 = l + delta
        if fun(f, step(x1)) < fun(f, step(x2)):
            r = x2
        else:
            l = x1
        func_counter += 2

    return (r + l) / 2, func_counter


# Градиентный спуск на основе золотого сечения
def golden_ratio_descent(f, start_point, eps=1e-5):
    func_counter = 0
    grad_counter = 0
    points = [start_point]
    counter = 0
    while True:
        counter += 1
        grad = gradient(f, points[-1])
        grad_counter += 1
        res = golden_ratio(f, points[-1], grad, eps)
        learning_rate = res[0]
        func_counter += res[1]
        new_point = [points[-1][i] - learning_rate * grad[i] for i in range(len(points[-1]))]
        if stop_condition(new_point, points[-1], eps):
            break
        points.append(new_point)
    return counter, points[-1], fun(f, points[-1]), points, func_counter, grad_counter


# Все реализованные методы
methods = ['constant', 'dichotomy', 'nelder_mead', 'golden_ratio']


# Вывод графика функции и её линий уровня
def draw(sym, f, Xs, Ys, name, points, counter):
    xs = [points[i][0] for i in range(counter)]
    ys = [points[i][1] for i in range(counter)]
    zs = [fun(sym, [xs[i], ys[i]]) for i in range(counter)]
    Zs = f(Xs, Ys)

    plt.figure()
    ax = plt.axes(projection='3d')
    ax.plot_surface(Xs, Ys, Zs, cmap='viridis', alpha=0.5)
    ax.set_xlabel('x')
    ax.set_ylabel('y')
    ax.set_zlabel('f')
    ax.scatter(xs, ys, zs, color='r', label='Траектория', alpha=0.8)
    ax.legend()
    plt.title("График функции. " + name)
    plt.show()

    plt.figure()
    plt.contour(Xs, Ys, Zs, levels=20, cmap='viridis')
    plt.plot(xs, ys, color='r')
    plt.scatter(xs, ys, color='r', label='Траектория')
    plt.annotate('Result', xy=(xs[-1], ys[-1]), xytext=(xs[-1] + 1, ys[-1] + 1),
                 arrowprops=dict(facecolor='black', shrink=0.05),
                 )
    plt.xlabel('x')
    plt.ylabel('y')
    plt.title("Линии уровня. " + name)
    plt.legend()
    plt.show()


# Основная функция для получения результатов работы методов на переданной функции
def test_method(f, start_point, method='constant', bounds=(-1, 1), eps=1e-5):
    assert method in methods

    if method == 'constant':
        res = gradient_descent(f, start_point, eps=eps)
    elif method == 'dichotomy':
        res = dichotomy_descent(f, start_point, eps)
    elif method == 'nelder_mead':
        res = nelder_mead(lambdify([symbols(f'x0:{len(start_point)}')], f, 'numpy'), np.array(start_point), eps)
    else:
        res = golden_ratio_descent(f, start_point, eps)

    counter, point, val, points, fev, gev = res

    print(method)
    print(f"Количество итераций: {counter}")
    print(f"Полученная точка: {point}")
    print(f"Полученное значение функции: {val}")
    print(f"Количество вычислений функции: {fev}")
    print(f"Количество вычислений градиента: {gev}")

    if len(start_point) == 2:
        Xs, Ys = np.meshgrid(np.linspace(bounds[0], bounds[1], 100), np.linspace(bounds[0], bounds[1], 100))
        draw(f, lambdify(["x0", "x1"], f, 'numpy'), Xs, Ys, method, points, counter)


# Тест функции для n переменных
def test_n_dimensional(n, method='constant'):
    variables = symbols(f'x0:{n}')
    function = sum(v**2 for v in variables)
    start_point = [-2] * n
    test_method(function, start_point, method=method, bounds=(-1, 1), eps=1e-7)


# Тест плохо обусловленной функции
def test_poorly_conditioned(method='constant'):
    X, Y = symbols('x0 x1')
    rosenbrock = (1 - X) ** 2 + 100 * (Y - X ** 2) ** 2
    test_method(rosenbrock, [-2, 1], method=method, bounds=(-5, 5), eps=1e-5)


# Тест функции с шумом
def test_noisy_function(method='constant'):
    X, Y = symbols('x0 x1')
    noisy_function = X ** 2 + Y ** 2 + random.uniform(-0.1, 0.1)
    test_method(noisy_function, [-2, 2], method=method, bounds=(-4, 4), eps=1e-5)


# Тест мультимодальной функции
def test_multimodal_function(method='constant'):
    X, Y = symbols('x0 x1')
    rastrigin = 10 * 2 + (X ** 2 - 10 * cos(2 * pi * X)) + (Y ** 2 - 10 * cos(2 * pi * Y))
    test_method(rastrigin, [-2, 2], method=method, bounds=(-30, 30), eps=1e-5)


if __name__ == '__main__':
    X, Y = symbols('x0 x1')

    f = X ** 2 + Y ** 2
    # f = -cos(X) * cos(Y) * exp(-((X - pi) ** 2 + (Y - pi) ** 2))
    # test_method(f=f, start_point=[-2, 2], method='constant', bounds=(-8, 8), eps=1e-5)

    # test_n_dimensional(3, method='constant')
    # test_poorly_conditioned(method='constant')
    # test_noisy_function(method='constant')
    # test_multimodal_function(method='constant')
