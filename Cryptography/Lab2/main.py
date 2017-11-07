import random
import time
import matplotlib.pyplot as plt
from lib.gcd import gcd_binary, gcd_euclid, gcd_factorization


def main():

    small_inputs = []
    large_inputs = []

    for i in range(10):
        digits = random.randint(1, 6)
        small_inputs.append((random.randint(10 ** digits, 10 ** (digits + 1)), random.randint(10 ** digits, 10 ** (digits + 1))))

    for i in range(10):
        digits = random.randint(1, 3000)
        large_inputs.append((random.randint(10 ** digits, 10 ** (digits + 1)), random.randint(10 ** digits, 10 ** (digits + 1))))

    results = {}
    results['factorization'] = []
    results['euclid'] = []
    results['binary'] = []

    for index in range(10):
        with_time('factorization', gcd_factorization, small_inputs[index][0], small_inputs[index][1], results)
        with_time('euclid', gcd_euclid, large_inputs[index][0], large_inputs[index][1], results)
        with_time('binary', gcd_binary, large_inputs[index][0], large_inputs[index][1], results)

    plot('factorization', results)
    plot('euclid', results)
    plot('binary', results)


def plot(alg_name, data):

    x = []
    y = []
    for d in sorted(data[alg_name], key=lambda x: x[1]):
        x.append(d[1])
        y.append(d[0])

    plt.title(alg_name)
    plt.xlabel("Digits")
    plt.ylabel("Time (ms)")
    plt.plot(x, y)
    plt.show()


def with_time(alg_name, fun, input1, input2, results):

    start = time.time()
    result = fun(input1, input2)
    end = time.time()

    print("{} gcd({}, {}) = {} in {} seconds".format(alg_name, input1, input2, result, end - start))

    results[alg_name].append(((end - start) * 1000, len("{}".format(input1))))


if __name__ == '__main__':
    main()