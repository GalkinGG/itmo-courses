from collections import defaultdict
import math

def calculate_conditional_entropy(N, data):
    joint_counts = defaultdict(int)
    x_counts = defaultdict(int)

    for x, y in data:
        joint_counts[(x, y)] += 1
        x_counts[x] += 1

    conditional_entropy = 0.0

    for (x, y), freq in joint_counts.items():
        conditional_entropy -= freq / N * math.log(freq / x_counts[x])

    return conditional_entropy


Kx, Ky = map(int, input().split())
N = int(input().strip())
data = [tuple(map(int, input().split())) for _ in range(N)]

result = calculate_conditional_entropy(N, data)

print(f"{result:.6f}")