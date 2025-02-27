import numpy as np


def calculate_weighted_pearson_correlation(N, K, a, b):
    b = np.array(b)

    counts = np.zeros(K + 1, dtype=int)
    for val in a:
        counts[val] += 1

    mean_b = np.mean(b)

    std_b = np.std(b)

    if std_b == 0:
        return 0.0

    weighted_sum_correlation = 0.0

    for category in range(1, K + 1):
        one_hot = np.array([1 if x == category else 0 for x in a])

        mean_one_hot = np.mean(one_hot)

        covariance = np.mean((one_hot - mean_one_hot) * (b - mean_b))
        std_one_hot = np.std(one_hot)

        if std_one_hot > 0:
            correlation = covariance / (std_one_hot * std_b)
        else:
            correlation = 0.0

        weight = counts[category] / N
        weighted_sum_correlation += weight * correlation

    return weighted_sum_correlation


N, K = map(int, input().split())
a = list(map(int, input().split()))
b = list(map(int, input().split()))

result = calculate_weighted_pearson_correlation(N, K, a, b)

print(f"{result:.9f}")