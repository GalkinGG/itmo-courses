prime_div(N, D) :-
    find_prime_div(N, 2, D).

find_prime_div(N, D, D) :-
    0 is mod(N,D).
find_prime_div(N, D, R) :-
    D < N,
    (0 is mod(N,D) ->
       (N1 is N//D, find_prime_div(N1, D, R))
    ;  (next_prime(D, D1), find_prime_div(N, D1, R))
    ).

prime_divs(N, L) :-
    findall(D, prime_div(N, D), L).

prime_divisors(N, L) :- prime_divs(N, R), L == R.
prime_divisors(N, X) :- prime_divs(N, X).

square_divisors(N, D) :- T is N*N, prime_divisors(T, D).

prime(N) :- \+ composite(N).

composite(N) :- composite_table(N).

next_prime(N, R) :- var(R), R is N + 1, prime(R), !.



next_prime(N, R) :- var(R), N1 is N + 1, next_prime(N1, R).

init(MAX_N, K, C) :- T is C+K, T > MAX_N.
init(MAX_N, N, C) :- T is C+N, T =< MAX_N, assert(composite_table(T)), C1 is C+N, init(MAX_N, N, C1).
init(MAX_N, K) :- K > MAX_N.
init(MAX_N, N) :- N =< MAX_N, init(MAX_N, N, N), N1 is N+1, init(MAX_N, N1).
init(MAX_N) :- init(MAX_N, 2).


