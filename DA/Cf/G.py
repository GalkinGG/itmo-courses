def gini_index_from_counts(total, sum_squares):
    if total == 0:
        return 0.0
    return 1.0 - sum_squares / (total * total)


def compute_gini_scores(N, K, classes):
    right_counts = [0] * (K + 1)
    for c in classes:
        right_counts[c] += 1
    left_counts = [0] * (K + 1)

    right_total = N
    left_total = 0
    right_sum_squares = sum(count ** 2 for count in right_counts)
    left_sum_squares = 0

    gini_scores = []

    for i in range(1, N):
        c = classes[i - 1]

        left_sum_squares += 2 * left_counts[c] + 1
        right_sum_squares -= 2 * right_counts[c] - 1

        left_counts[c] += 1
        right_counts[c] -= 1
        left_total += 1
        right_total -= 1

        gini_left = gini_index_from_counts(left_total, left_sum_squares)
        gini_right = gini_index_from_counts(right_total, right_sum_squares)

        weighted_gini = (left_total / N) * gini_left + (right_total / N) * gini_right
        gini_scores.append(weighted_gini)

    return gini_scores


N, K = map(int, input().strip().split())
classes = list(map(int, input().strip().split()))

gini_scores = compute_gini_scores(N, K, classes)
for score in gini_scores:
    print(f"{score:.9f}")