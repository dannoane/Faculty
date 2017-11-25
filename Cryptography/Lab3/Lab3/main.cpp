//
//  main.cpp
//  Lab3
//
//  Created by Noane Dan on 23/11/2017.
//  Copyright © 2017 Noane Dan. All rights reserved.
//

#include <iostream>
#include <cstdlib>
#include <ctime>
#include <cmath>

void factor(int n, int *r, int *d) {
    
    int f = 1, p = 0;
    
    while ((n % (f * 2)) == 0) {
        f *= 2;
        ++p;
    }
    
    *r = p;
    *d = n / f;
}

double miller_rabin(int n, int k) {
    
    int r, d, a, x;
    int i, j;
    
    factor(n - 1, &r, &d);
    printf("%d %d\n", r, d);
    
    for (i = 0; i < k; ++i) {
        a = (rand() % (n - 4)) + 2;
        x = (int) pow(a, d) % n;
        
        if (x == 1 || x == (n - 1)) {
            continue;
        }
        
        for (j = 0; j < (r - 1) && x != (n - 1); ++j) {
            x = (int) pow(x, 2) % n;
            
            if (x == 1) {
                return 0;
            }
        }
        
        if (j == r - 1) {
            return 0;
        }
    }
    
    return 1 - (1 / pow(2, k));
}

int main(int argc, const char * argv[]) {
    
    srand((unsigned int) time(0));
    printf("%f\n", miller_rabin(689, 1));
    
    return 0;
}
