import numpy as np

n = int(input())

objects = []
for _ in range(n):
    x, y = map(int, input().split())
    objects.append((x, y))
objects = np.array(objects)

m = int(input())

queries = []
for _ in range(m):
    xq, kq = map(int, input().split())
    queries.append((xq, kq))

results = []

for xq, kq in queries:
    distances = np.abs(objects[:, 0] - xq)

    sorted_indices = np.argsort(distances)

    if kq > n:
        results.append(-1)
    else:
        nearest_targets = objects[sorted_indices[:kq], 1]

        if kq < n and distances[sorted_indices[kq - 1]] == distances[sorted_indices[kq]]:
            results.append(-1)
        else:
            avg_target = np.mean(nearest_targets)
            results.append(avg_target)

for result in results:
    print(result)

 