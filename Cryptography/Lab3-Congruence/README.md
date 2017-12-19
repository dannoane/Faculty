### Lab 3

#### Definition:
Two integers, __a__ and __b__, are congruent modulo __n__ if __n__
is not zero, and __a__ and __b__ give the same remainder when divided
by __n__.

### Algorithm:
1. We reduce __a__ and __b__ by __n__. (using modulo)
2. If __gcd(a, n)|b__ then we divide __a__ and __b__ by it.
3. We find __x__ = __a^-1 * b__.
4. The solution is: __a * x is congruent to b (modulo n)__.