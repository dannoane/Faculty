### Lab 4

#### Algorithm:
Find a nontrivial factor of __n__ (and odd composite number which is not a square)

1. For __k__ times (where __k__ is a coefficient given by the user)
repeat the next steps:
2. Let __t0__ be __[sqrt(k * n)]__.
3. For __t__ from __t0 + 1__ to __t0 + B__ (where __B__ is a choosen bound) do:
4. If __t^2 - k * n__ is a square __s^2_ then __n = 1/k * (t - s)(t + s)__, else repeat.
5. __t + s__ is a nontrivial factor of __n__.