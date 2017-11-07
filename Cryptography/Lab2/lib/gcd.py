
def gcd_euclid(a, b):

    while b != 0:
        a, b = b, a % b

    return a


def gcd_binary(a, b):

    if a == 0:
        return b
    if b == 0:
        return a

    shift = 0
    while ((a | b) & 1) == 0:
        a >>= 1
        b >>= 1
        shift += 1

    while (a & 1) == 0:
        a >>= 1

    while True:
        while (b & 1) == 0:
            b >>= 1

        if a > b:
            a, b = b, a

        b = b - a

        if b == 0:
            break

    return a << shift


def factorization(n):

    factors = []

    f = 2
    while f <= n:
        p = 0

        while n % f == 0:
            n /= f
            p += 1

        if p != 0:
            factors.append((f, p))

        f += 1

    return factors


def find_factor(factors, factor):

    result = list(filter(lambda x: x[0] == factor, factors))

    if len(result) > 0:
        return result[0]

    return None


def gcd_factorization(a, b):

    factors_a = factorization(a)
    factors_b = factorization(b)

    gcd = 1
    for factor_a in factors_a:
        factor_b = find_factor(factors_b, factor_a[0])
        if factor_b:
            gcd *= factor_a[0] ** min(factor_a[1], factor_b[1])

    return gcd
