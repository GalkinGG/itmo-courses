import math

import optuna
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import Normalize
from sympy import Symbol, lambdify, parsing


# Основное задание 1 и 2
def artificial_bee_colony(
        f,
        low,
        high,
        num_bees=20,
        num_elite=5,
        iterations=500,
        num_best_sites=10,
        neighborhood_size=0.1,
        callback=None
):
    it = 0

    fitness = [None]
    bees = np.random.uniform(low, high, (num_bees, 2))

    if callback:
        callback(bees)

    for _ in range(iterations):
        it += 1

        fitness = np.array([f(bee[0], bee[1]) for bee in bees])

        sorted_indices = np.argsort(fitness)
        bees = bees[sorted_indices]
        fitness = fitness[sorted_indices]

        for i in range(num_elite, num_bees):
            bees[i] = np.random.uniform(low, high, 2)

        for i in range(num_elite):
            for j in range(num_best_sites):
                new_bee = bees[i] + np.random.uniform(
                    -neighborhood_size, neighborhood_size, 2)
                new_fitness = f(new_bee[0], new_bee[1])
                if new_fitness < fitness[i]:
                    bees[i] = new_bee
                    fitness[i] = new_fitness

        if callback:
            callback(bees)

    best_solution = bees[0]
    best_fitness = fitness[0]

    return {'x': best_solution, 'fun': best_fitness, 'it': it}


# Доп задание 1
def optuna_minimize(f, callback, l, r, n):
    def objective(trial):
        x = trial.suggest_float('x', l, r)
        y = trial.suggest_float('y', l, r)

        return f(x, y)

    study = optuna.create_study(direction='minimize')
    study.optimize(objective, n_trials=n, callbacks=[callback])

    return {'x': study.best_params, 'y': study.best_value}


# Доп задание 2
np.random.seed(234)
X = 2 * np.random.rand(200, 1)
Y = 6 * X + np.random.randn(200, 1) + 12

start = (np.zeros((1,)), np.zeros((1,)))


def loss_function(w, b, X, Y):
    pred = np.dot(X, w) + b
    return np.mean((pred - Y) ** 2)


def newton_method(f, x, grad, hess, eps=1e-7,
                  method='standard', callback=None):
    methods = {
        'standard': lambda *args: 1,
    }

    it = 0

    while True:
        it += 1

        if callback:
            callback(x)

        gradient = grad(x)
        hessian_inv = np.linalg.inv(hess(x))

        delta = hessian_inv.dot(np.transpose(gradient))

        x_prev = x
        x = x - methods[method](f, x, delta, grad) * delta

        if (np.linalg.norm(delta) < eps or
                np.linalg.norm(x - x_prev) < eps):
            return {'x': x, 'fun': f(x), 'it': it}


def objective_newton(trial):
    eps = trial.suggest_float('eps', 1e-12, 1e-1, log=True)

    def f(x):
        pred = np.dot(X, x[0]) + x[1]
        return np.mean((pred - Y) ** 2)

    def grad(x):
        pred = np.dot(X, x[0]) + x[1]
        error = pred - Y
        grad_w = 2 * np.mean(X * error)
        grad_b = 2 * np.mean(error)
        return np.array([grad_w, grad_b])

    def hess(x):
        hess_w = 2 * np.mean(X ** 2)
        hess_b = 2
        return np.array([[hess_w, 0], [0, hess_b]])

    result = newton_method(f, np.array([start[0][0], start[1][0]]),
                           grad, hess, eps=eps, method='standard')

    return result['fun']


def study_method(method):
    study = optuna.create_study(direction='minimize')
    study.optimize(method, n_trials=100)


def level_lines(x, surface_func, bounds=(-10, 10), num=100, levels=10):
    fig = plt.figure()
    axes = fig.add_subplot(111)
    axes.set_xlabel('X')
    axes.set_ylabel('Y')
    axes.set_title('Level lines')

    xgrid, ygrid = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                               np.linspace(bounds[0], bounds[1], num))

    norm = Normalize(vmin=0, vmax=len(x))
    for i in range(len(x)):
        color = plt.cm.cool(norm(i))
        plt.scatter(*x[i], s=10, color=color)

    axes.contour(xgrid, ygrid, surface_func([xgrid, ygrid]), levels=levels)


def create_surface(x_val, fun_val, surface_func, bounds=(-10, 10), num=100):
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    X, Y = np.meshgrid(np.linspace(bounds[0], bounds[1], num),
                       np.linspace(bounds[0], bounds[1], num))
    Z = surface_func(np.array([X, Y]))

    ax.plot_surface(X, Y, Z, cmap='BuPu')
    colors = np.linspace(0, 1, len(x_val))

    for i in range(len(x_val)):
        ax.plot(*x_val[i], fun_val[i], '.',
                label='top', zorder=4, markersize=5, c=plt.cm.cool(colors[i]))


def main():
    x, y = Symbol('x'), Symbol('y')
    # Функция Розенброка:
    # parse = '(1 - x)**2 + 100 * (y - x**2)**2'
    # Функция Химмельблау:
    parse = '(x**2 + y - 11)**2 + (x + y**2 - 7)**2'
    # Функция Изома:
    # parse = '-cos(x) * cos(y) * exp(-((x - pi)**2 + (y - pi)**2))'

    foo = parsing.parse_expr(parse)

    f = lambdify((x, y), foo)

    x_val, fun_val = [], []

    def bees_callback(bees):
        for bee in bees:
            x_val.append(bee)
            fun_val.append(f(*bee))

    result = artificial_bee_colony(f, 0, 2 * math.pi, num_bees=10,
                                   num_elite=3, iterations=5)

    print(result)

    def optuna_callback(hist, _):
        x_val.append(list(hist.best_params.values()))
        fun_val.append(hist.best_value)

    result = optuna_minimize(f, optuna_callback, -5, 5, 350)

    print(result)

    # study_method(objective_newton)

    if x_val and fun_val:
        level_lines(x_val, lambda vec: f(*vec), bounds=(-5, 5), levels=10)

        create_surface(x_val, fun_val, lambda vec: f(*vec), bounds=(-5, 5))

        plt.show()


if __name__ == '__main__':
    main()
