from collections import defaultdict


def dist(xs):
    n = len(xs)
    pref = [0]

    for i in range(1, n + 1):
        pref.append(pref[i - 1] + xs[i - 1])

    return sum(pref[n] - (pref[i + 1] + pref[i]) + xs[i] * (2 * i - n + 1) for i in range(n))


_, n = int(input()), int(input())

classes = defaultdict(list)
xs = []
for i in range(n):
    x, y = map(int, input().split())
    classes[y].append(x)
    xs.append(x)
xs.sort()

in_dist = sum(dist(sorted(classes[y])) for y in classes)
print(in_dist)
print(dist(xs) - in_dist)
