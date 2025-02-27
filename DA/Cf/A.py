import numpy as np


def calculate_matrix(l, ms):
    result = []
    for t in l:
        row = [1]
        for m in ms:
            row.append(np.sin(2 * np.pi * t / m))
            row.append(np.cos(2 * np.pi * t / m))
        result.append(row)
    return np.array(result)


N = 168
ms = [12, 24, 168, 672]
input_data = np.array([int(input()) for _ in range(N)])

lstsq = np.linalg.lstsq(
    calculate_matrix(np.arange(1, N + 1), ms),
    input_data
)

args = lstsq[0]

print(*calculate_matrix(np.arange(N + 1, 2 * N + 1), ms) @ args, sep='\n')