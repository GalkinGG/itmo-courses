import numpy as np
from sympy import Symbol, lambdify, parsing
from scipy.optimize import minimize
import matplotlib.pyplot as plt
from matplotlib.colors import Normalize


# Реализация одномерного поиска (дихотомии)
def dichotomy(fun, x0, grad, _, eps=1e-6):
    l = eps
    r = 100
    delta = eps / 2

    def foo(xi): return [x0[i] - xi * grad[i] for i in range(len(x0))]

    while r - l >= eps:
        x1 = (l + r - delta) / 2
        x2 = (l + r + delta) / 2
        if fun(foo(x1)) >= fun(foo(x2)):
            l = x1
        else:
            r = x2

    return (r + l) / 2


# Реализация одномерного поиска по правилу Вольфе
def wolfe(fun, x0, delta, grad, c1=1e-4, c2=0.9, iterations=100):
    alpha, alpha_min, alpha_max = 1.0, 0.0, np.inf
    f_value, grad_value = fun(x0), grad(x0)
    delta = -delta

    for i in range(iterations):
        x_next = x0 + alpha * delta
        f_value_next = fun(x_next)
        if f_value_next <= f_value + c1 * alpha * np.dot(grad_value, delta):
            if np.dot(grad(x_next), delta) >= c2 * np.dot(grad_value, delta):
                break
            else:
                alpha_min, alpha = alpha, (alpha_min + alpha_max) / 2
        else:
            alpha_max, alpha = alpha, (alpha_min + alpha_max) / 2
        alpha /= 2

    return alpha


methods = {
    'constant': lambda *args: 1,  # Основное задание 1
    'dichotomy': dichotomy,  # Основное задание 2
    'wolfe': wolfe  # Дополнительное задание 1
}


# Реализация метода Ньютона
def newton_method(fun, x0, grad, hess, eps=1e-6, method='constant', callback=None):
    assert method in methods.keys()

    method = methods[method]
    count_iteration = count_fun = count_grad = count_hess = 0

    def cnt_fun(vec):
        nonlocal count_fun
        count_fun += 1
        return fun(vec)

    def cnt_grad(vec):
        nonlocal count_grad
        count_grad += 1
        return grad(vec)

    def cnt_hess(vec):
        nonlocal count_hess
        count_hess += 1
        return hess(vec)

    while True:
        count_iteration += 1

        if callback:
            callback(x0)

        gradient = cnt_grad(x0)
        inverse_hessian_matrix = np.linalg.inv(cnt_hess(x0))
        delta = inverse_hessian_matrix.dot(np.transpose(gradient))
        x_prev, x0 = x0, x0 - method(cnt_fun, x0, delta, cnt_grad) * delta

        if np.linalg.norm(delta) < eps or np.linalg.norm(x0 - x_prev) < eps:
            return {
                'x': [*x0],
                'f': cnt_fun(x0),
                'count_iteration': count_iteration,
                'count_fun': count_fun - 1,
                'count_grad': count_grad,
                'count_hess': count_hess
            }


# Основное задание 3. scipy.optimize: Метод Newton-CG и квазиньютоновский метод BFGS
def scipy_newton_method(fun, x0, jac, eps=1e-6, method='Newton-CG', callback=None):
    assert method in ('Newton-CG', 'BFGS')
    return minimize(fun, x0, jac=jac, tol=eps, method=method, callback=callback)


# Построение линий уровня
def create_level_lines(xs, surface_func, bounds=(-10, 10), num=100):
    fig = plt.figure()
    xgrid, ygrid = np.meshgrid(np.linspace(bounds[0], bounds[1], num), np.linspace(bounds[0], bounds[1], num))
    axes = fig.add_subplot(111)

    axes.set_title('Линии уровня')
    axes.set_xlabel('x')
    axes.set_ylabel('y')

    norm = Normalize(vmin=0, vmax=len(xs))

    for i in range(len(xs)):
        color = plt.cm.cool(norm(i))
        plt.scatter(*xs[i], s=30, color=color)
    axes.contour(xgrid, ygrid, surface_func([xgrid, ygrid]), levels=20, cmap='viridis')


# Построение графика функции
def create_surface(xs, fs, surface_func, borders=(-10, 10), num=100):
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    X, Y = np.meshgrid(np.linspace(borders[0], borders[1], num), np.linspace(borders[0], borders[1], num))
    Z = surface_func(np.array([X, Y]))
    ax.plot_surface(X, Y, Z, cmap='viridis')

    ax.set_title('График функции')
    ax.set_xlabel('x')
    ax.set_ylabel('y')
    ax.set_zlabel('f')

    colors = np.linspace(0, 1, len(xs))
    for i in range(len(xs)):
        ax.plot(*xs[i], fs[i], '.', label='top', zorder=4, markersize=5, c=plt.cm.cool(colors[i]))


if __name__ == '__main__':
    x, y = Symbol('x'), Symbol('y')
    parse = '(1 - x)**2 + 100 * (y - x**2)**2'  # Функция Розенброка
    # parse = '-cos(x) * cos(y) * exp(-((x - pi)**2 + (y - pi)**2))' # Функция Изома
    # parse = '(x**2 + y - 11)**2 + (x + y**2 - 7)**2'  # Функция Химмельблау
    foo = parsing.parse_expr(parse)

    x_value, f_value = [], []
    f = lambdify((x, y), foo)

    f_x = lambdify((x, y), foo.diff(x))
    f_y = lambdify((x, y), foo.diff(y))

    f_x_x = lambdify((x, y), foo.diff(x).diff(x))
    f_x_y = lambdify((x, y), foo.diff(x).diff(y))
    f_y_x = lambdify((x, y), foo.diff(y).diff(x))
    f_y_y = lambdify((x, y), foo.diff(y).diff(y))

    def fun(vec): return f(*vec)
    def fun_jac(vec): return np.array([f_x(*vec), f_y(*vec)])
    def fun_hess(vec): return np.array([[f_x_x(*vec), f_x_y(*vec)], [f_y_x(*vec), f_y_y(*vec)]])
    def callback(hist): x_value.append(hist), f_value.append(fun(hist))

    result = newton_method(fun, np.array([-2, 1]), fun_jac, fun_hess, eps=1e-6, method='constant', callback=callback)
    # result = scipy_newton_method(fun, np.array([-4, 0]), jac=fun_jac, eps=1e-6, method='BFGS', callback=callback)

    print(result)

    if x_value and f_value:
        create_level_lines(x_value, lambda vec: fun(vec))
        create_surface(x_value, f_value, lambda vec: fun(vec))
        plt.show()
