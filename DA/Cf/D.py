import numpy as np
from collections import defaultdict


def calculate_conditional_dispersion_expectation(K, N, data):
    categories = defaultdict(list)
    for x, y in data:
        categories[x].append(y)

    total_objects = N

    expectation_conditional_dispersion = 0.0
    for x, values in categories.items():
        values = np.array(values)
        mean_y = np.mean(values)
        variance_y = np.mean((values - mean_y) ** 2)

        # Вероятность P(X=x)
        probability_x = len(values) / total_objects

        expectation_conditional_dispersion += probability_x * variance_y

    return expectation_conditional_dispersion


K = int(input().strip())
N = int(input().strip())
data = [tuple(map(int, input().strip().split())) for _ in range(N)]

result = calculate_conditional_dispersion_expectation(K, N, data)

print(f"{result:.6f}")