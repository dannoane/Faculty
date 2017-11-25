#  Lab 3

## Implement the Miller-Rabin algorithm for testing primality of numbers

### Steps:
1.  Write __n - 1__ (where __n__ is the number to be tested) as __2^r * d__
2. Perform steps __3, 4, 5__ __k__ times (this parameter determines the accuracy of the test)
3. Pick a random number __a__ in the interval __[2, n - 2]__
4. Compute (by the repeated squaring modular exponentiation) the following sequence (modulo __n__): __a^d__, __a^2*d__, ..., __a^r*d__
5. If the first number in the sequence is __1__ or if one gets the value __1__ and its previous number __-1__, then __n__ is possible
    to be prime and go to step __3__ else go to next step.
6. The algorithm stops and __n__ is composite.

