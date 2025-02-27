import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt
from memory_profiler import profile

learning_rate = {
    'standard': lambda *args: 1e-2,
    'step_decay': lambda k: 0.1 * (0.9 ** (k // 100)),
    'linear_decay': lambda k: max(1e-3, 1e-1 - k * 1e-3)
}


# Основное задание №1, №2
def stochastic_gradient_descent(x, y, start, learning_rate_method='standard', batch_size=32, count_epochs=20):
    assert learning_rate_method in learning_rate

    it = 0
    w, b = start

    for epoch in range(count_epochs):
        for i in range(0, x.shape[0], batch_size):
            it += 1

            xi = x[i:i + batch_size]
            yi = y[i:i + batch_size]

            h = learning_rate[learning_rate_method](it)

            predict = np.dot(xi, w) + b

            w = w - h * (2 / batch_size) * np.dot(xi.T, predict - yi)
            b = b - h * (2 / batch_size) * np.sum(predict - yi)

    return {'w': w, 'b': b, 'it': it}


optimizers = {
    'SGD': tf.optimizers.legacy.SGD(learning_rate=0.01),
    'Momentum': tf.optimizers.legacy.SGD(learning_rate=0.01, momentum=0.9),
    'Nesterov': tf.optimizers.legacy.SGD(learning_rate=0.01, momentum=0.9, nesterov=True),
    'AdaGrad': tf.optimizers.legacy.Adagrad(learning_rate=2.0),
    'RMSProp': tf.optimizers.legacy.RMSprop(learning_rate=0.01),
    'Adam': tf.optimizers.legacy.Adam(learning_rate=0.01)
}


# Основное задание №3
@profile
def tensorflow_optimizer(X, Y, optimizer_name='SGD', batch_size=32, count_epochs=30):
    assert optimizer_name in optimizers

    X = tf.constant(X, dtype=tf.float32)
    Y = tf.constant(Y, dtype=tf.float32)
    w = tf.Variable(tf.random.normal([X.shape[1], 1]), name='weight')
    b = tf.Variable(tf.random.normal([1]), name='bias')

    def model(x):
        return tf.matmul(x, w) + b

    def loss_fun(y_true, y_pred):
        return tf.reduce_mean(tf.square(y_true - y_pred))

    optimizer = optimizers[optimizer_name]

    for epoch in range(count_epochs):
        for i in range(0, X.shape[0], batch_size):
            xi = X[i:i + batch_size]
            yi = Y[i:i + batch_size]
            with tf.GradientTape() as tape:
                y_pred = model(xi)
                loss = loss_fun(yi, y_pred)
            gradients = tape.gradient(loss, [w, b])
            optimizer.apply_gradients(zip(gradients, [w, b]))

    return w.numpy(), b.numpy()


def create_2d_linear_plot(x, y, w, b):
    fig, ax = plt.subplots()
    for point in zip(x, y):
        ax.scatter(*point, color='b')
    ax.plot(x, w * x + b, color='r')


def create_3d_linear_plot(x, y, z, w, b):
    fig = plt.figure()
    ax = fig.add_subplot(projection='3d')
    for point in zip(x, y, z):
        ax.scatter(*point, s=0.1, color='b')
    x_grid, y_grid = np.meshgrid(np.linspace(x.min(), x.max(), 100), np.linspace(y.min(), y.max(), 100))
    z_grid = w[0] * x_grid + w[1] * y_grid + b
    ax.plot_wireframe(x_grid, y_grid, z_grid, alpha=0.5, rstride=20, cstride=20, color='black')


if __name__ == '__main__':
    """
    Stochastic gradient descent:
    np.random.seed(234)
    X = 2 * np.random.rand(200, 1)
    Y = 6 * X + np.random.randn(200, 1) + 12
    """

    # np.random.seed(234)
    # X = 2 * np.random.rand(200, 1)
    # Y = 6 * X + np.random.randn(200, 1) + 12
    #
    # start = (np.array([[0]]), np.array([0]))
    #
    # result = stochastic_gradient_descent(X, Y, start, learning_rate_method='step_decay', batch_size=32, count_epochs=20)
    #
    # print(result)
    #
    # create_2d_linear_plot(X, Y, result['w'], result['b'])
    # plt.show()

    """
    SGD and Modifications:
    np.random.seed(543)
    X = -1.43 * 1.45 * np.random.rand(10000, 1)
    Y = 7 * X + 2 * np.random.rand(10000, 1)
    Z = 2 * X + 5 * Y + 10 * np.random.rand(10000, 1) + 3
    """

    np.random.seed(543)
    X = -1.43 * 1.45 * np.random.rand(10000, 1)
    Y = 7 * X + 2 * np.random.rand(10000, 1)
    Z = 2 * X + 5 * Y + 10 * np.random.rand(10000, 1) + 3

    data = np.hstack((X, Y))

    w, b = tensorflow_optimizer(data, Z, optimizer_name='Adam', batch_size=32, count_epochs=10)

    print('w =', w)
    print('b =', b)

    # create_2d_linear_plot(Y, Z, w[1], b)
    # create_3d_linear_plot(X, Y, Z, w, b)
    # plt.show()
