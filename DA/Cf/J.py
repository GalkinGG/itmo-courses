def spearman_rank_correlation(n, data):
    sorted_x1 = sorted(range(n), key=lambda i: data[i][0])
    rank_x1 = [0] * n
    for rank, idx in enumerate(sorted_x1):
        rank_x1[idx] = rank + 1

    sorted_x2 = sorted(range(n), key=lambda i: data[i][1])
    rank_x2 = [0] * n
    for rank, idx in enumerate(sorted_x2):
        rank_x2[idx] = rank + 1

    d_square_sum = sum((rank_x1[i] - rank_x2[i]) ** 2 for i in range(n))

    spearman = 1 - (6 * d_square_sum) / (n * (n ** 2 - 1))
    return spearman


n = int(input())
data = [tuple(map(int, input().split())) for _ in range(n)]
result = spearman_rank_correlation(n, data)

print(f"{result:.9f}")