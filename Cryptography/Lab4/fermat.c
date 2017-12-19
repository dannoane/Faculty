#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define B 10000000000

long fermat(long n, long c) {

    long t, t0, s, sq, k;

    for (k = 1; k <= c; ++k) {
        t0 = (long) sqrt(k * n);

        for (t = t0 + 1; t < t0 + B; ++t) {
            s = (t * t) - (k * n);
            sq = (long) sqrt(s);

            if (sq * sq == s) {
                if (((t + sq) % k) == 0) {
                    return (t + sq) / k;
                }
                else if (((t - sq) % k) == 0) {
                    return (t - sq) / k;
                }
            }
        }
    }

    return 1;
}

int main() {

    long n, k, f;
    
    printf("%s", "Enter an longger: ");
    scanf("%ld", &n);

    printf("%s", "Enter the coefficient ");
    scanf("%ld", &k);

    if (n % 2 == 0 || (long) sqrt(n) * (long) sqrt(n) == n) {
        printf("Enter an odd number which is not a square!\n");
        return -1;
    }

    while (n != 1) {
        f = fermat(n, k);
        n /= f;
        
        printf("F: %ld\n", f);
    }

    return 0;
}