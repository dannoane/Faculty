#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int gcd(int a, int b) {

    int r;
    while (b != 0) {
        r = a % b;
        a = b;
        b = r;
    }

    return a;
}

int mod_inverse(int a, int b) {
    
    int x;

    a %= b;

    for (x = 1; x < b; ++x) {
        if ((a * x) % b == 1) {
            return x;
        }
    }
}

int solve(int a, int b, int m) {

    int p, q, gcd_a_m;

    a %= m;
    b %= m;

    gcd_a_m = gcd(a, m);

    
    if ((b % gcd_a_m) != 0) {
        return -1;
    }

    a /= gcd_a_m;
    b /= gcd_a_m;
    m /= gcd_a_m;

    return (mod_inverse(a, m) * b) % m;
}

int main() {

    srand(time(NULL));

    int a, b, m;
    a = 4;//rand() % 100;
    b = 6;//rand() % 100;
    m = 9;//rand() % 1000;

    int result = solve(a, b, m);
    if (result != -1) {
        printf("%d * %d is congruent to %d modulo %d\n", a,result, b, m);
    }
    else {
        printf("No solution\n");
    }

    return 0;
}