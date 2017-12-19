#include <iostream>
#include <cstring>
#include <algorithm>
#include <mpi.h>

#define SIZE 100
#define COEFFICIENT_SIZE 5


class Polynomial {

public:
    unsigned int size;
    int* polynomial;


    Polynomial() {}

    Polynomial(unsigned int size) {
        
        this->size = size;
        this->polynomial = new int[size];

        for (int index = 0; index < this->size; ++index) {
            this->polynomial[index] = 0;
        }
    }

    Polynomial(unsigned int size, int *polynomial) {
        
        this->size = size;
        this->polynomial = new int[size];

        memcpy(this->polynomial, polynomial, size * sizeof(int));
    }

    int get(unsigned int index) {
        return this->polynomial[index];
    }

    void set(unsigned int index, int value) {
        this->polynomial[index] = value;
    }

    unsigned int getSize() {
        return this->size;
    }

    int* getPolynomial() {
        return this->polynomial;
    }

    friend Polynomial operator+(const Polynomial& first, const Polynomial& second) {

        Polynomial result;

        result.size = std::max(first.size, second.size);
        result.polynomial = new int[result.size];

        int index;
        for (index = 0; index < first.size && index < second.size; ++index) {
            result.polynomial[index] = first.polynomial[index] + second.polynomial[index];
        }

        while (index < first.size) {
            result.polynomial[index] = first.polynomial[index];
            ++index;
        }

        while (index < second.size) {
            result.polynomial[index] = second.polynomial[index];
            ++index;
        }

        return result;
    }

    friend Polynomial operator-(const Polynomial& first, const Polynomial& second) {

        Polynomial result;

        result.size = std::max(first.size, second.size);
        result.polynomial = new int[result.size];

        int index;
        for (index = 0; index < first.size && index < second.size; ++index) {
            result.polynomial[index] = first.polynomial[index] - second.polynomial[index];
        }

        while (index < first.size) {
            result.polynomial[index] = first.polynomial[index];
            ++index;
        }

        while (index < second.size) {
            result.polynomial[index] = -second.polynomial[index];
            ++index;
        }

        return result;
    }

    friend Polynomial operator*(const Polynomial& first, const Polynomial& second) {

        Polynomial result;

        result.size = first.size + second.size;
        result.polynomial = new int[result.size];

        for (int index = 0; index < result.size; ++index) {
            result.polynomial[index] = 0;
        }

        for (int indexA = 0; indexA < first.size; ++indexA) {
            for (int indexB = 0; indexB < second.size; ++indexB) {
                result.polynomial[indexA + indexB] += first.polynomial[indexA] * second.polynomial[indexB];
            }
        }

        return result;
    }

    friend Polynomial operator<<(const Polynomial& p, unsigned int value) {

        Polynomial result;

        result.size = p.size + value;
        result.polynomial = new int[result.size];

        for (int index = 0; index < result.size; ++index) {
            result.polynomial[index] = 0;
        }

        memcpy(result.polynomial + value, p.polynomial, p.size * sizeof(int));

        return result;
    }

    void print() {

        for (int index = 0; index < this->size; ++index) {
            std::cout << this->polynomial[index] << " ";
        }
        std::cout << "\n";
    }
};


void random_init(Polynomial& p) {

    for (int index = 0; index < p.getSize(); ++index) {
        p.set(index, rand() % COEFFICIENT_SIZE);
    }
}


Polynomial karatsuba(Polynomial a, Polynomial b, int me, int procs) {

    int length, half, next;

    if (a.getSize() <= 1 || b.getSize() <= 1) {
        return a * b;
    }

    length = std::max(a.getSize(), b.getSize());
    half = length / 2;

    Polynomial
        lowA(half, a.getPolynomial()),
        highA(a.getSize() - half, a.getPolynomial() + half),
        lowB(half, b.getPolynomial()),
        highB(b.getSize() - half, b.getPolynomial() + half);

    Polynomial lowHighA, lowHighB;
    lowHighA = lowA + highA;
    lowHighB = lowB + highB;

    Polynomial *result1, *result2, *result3;
    next = 3 * me + 1;

    if (procs >= next + 2) {
        MPI_Send(&lowA.size, 1, MPI_INT, next, 1, MPI_COMM_WORLD);
        MPI_Send(lowA.getPolynomial(), lowA.getSize(), MPI_INT, next, 2, MPI_COMM_WORLD);
        MPI_Send(&lowB.size, 1, MPI_INT, next, 3, MPI_COMM_WORLD);
        MPI_Send(lowB.getPolynomial(), lowB.getSize(), MPI_INT, next, 4, MPI_COMM_WORLD);

        MPI_Send(&lowHighA.size, 1, MPI_INT, next + 1, 1, MPI_COMM_WORLD);
        MPI_Send(lowHighA.getPolynomial(), lowHighA.getSize(), MPI_INT, next + 1, 2, MPI_COMM_WORLD);
        MPI_Send(&lowHighB.size, 1, MPI_INT, next + 1, 3, MPI_COMM_WORLD);
        MPI_Send(lowHighB.getPolynomial(), lowHighB.getSize(), MPI_INT, next + 1, 4, MPI_COMM_WORLD);

        MPI_Send(&highA.size, 1, MPI_INT, next + 2, 1, MPI_COMM_WORLD);
        MPI_Send(highA.getPolynomial(), highA.getSize(), MPI_INT, next + 2, 2, MPI_COMM_WORLD);
        MPI_Send(&highB.size, 1, MPI_INT, next + 2, 3, MPI_COMM_WORLD);
        MPI_Send(highB.getPolynomial(), highB.getSize(), MPI_INT, next + 2, 4, MPI_COMM_WORLD);

        unsigned int size1, size2, size3;
        int *polynomial1, *polynomial2, *polynomial3;
        MPI_Status status;
        
        MPI_Recv(&size1, 1, MPI_INT, next, 5, MPI_COMM_WORLD, &status);
        MPI_Recv(&size2, 1, MPI_INT, next + 1, 5, MPI_COMM_WORLD, &status);
        MPI_Recv(&size3, 1, MPI_INT, next + 2, 5, MPI_COMM_WORLD, &status);

        polynomial1 = new int[size1];
        polynomial2 = new int[size2];
        polynomial3 = new int[size3];

        MPI_Recv(polynomial1, size1, MPI_INT, next, 6, MPI_COMM_WORLD, &status);
        MPI_Recv(polynomial2, size2, MPI_INT, next + 1, 6, MPI_COMM_WORLD, &status);
        MPI_Recv(polynomial3, size3, MPI_INT, next + 2, 6, MPI_COMM_WORLD, &status);
        
        result1 = new Polynomial(size1, polynomial1);
        result2 = new Polynomial(size2, polynomial2);
        result3 = new Polynomial(size3, polynomial3);
    }
    else {
        result1 = new Polynomial();
        result2 = new Polynomial();
        result3 = new Polynomial();

        *result1 = karatsuba(lowA, lowB, me, procs);
        *result2 = karatsuba(lowHighA, lowHighB, me, procs);
        *result3 = karatsuba(highA, highB, me, procs);
    }

    return ((*result3) << (2 * half)) + (((*result2) - (*result3) - (*result1)) << half) + (*result1);
}


void karatsubaMaster(Polynomial a, Polynomial b, int procs) {

    a.print();
    b.print();

    Polynomial p;
    p = karatsuba(a, b, 0, procs);
    p.print();
}


void karatsubaWorker(int me, int procs) {

    unsigned int sizeA, sizeB;
    int *polynomialA, *polynomialB;
    MPI_Status status;

    MPI_Recv(&sizeA, 1, MPI_INT, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
    polynomialA = new int[sizeA];
    MPI_Recv(polynomialA, sizeA, MPI_INT, MPI_ANY_SOURCE, 2, MPI_COMM_WORLD, &status);

    MPI_Recv(&sizeB, 1, MPI_INT, MPI_ANY_SOURCE, 3, MPI_COMM_WORLD, &status);
    polynomialB = new int[sizeB];
    MPI_Recv(polynomialB, sizeB, MPI_INT, MPI_ANY_SOURCE, 4, MPI_COMM_WORLD, &status);

    Polynomial a(sizeA, polynomialA), b(sizeB, polynomialB);
    Polynomial p = karatsuba(a, b, me, procs);

    MPI_Send(&p.size, 1, MPI_INT, status.MPI_SOURCE, 5, MPI_COMM_WORLD);
    MPI_Send(p.getPolynomial(), p.getSize(), MPI_INT, status.MPI_SOURCE, 6, MPI_COMM_WORLD);
}


int main() {

    srand((unsigned int) time(0));
    
    MPI_Init(0, 0);

    int me, size;

    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &me);

    std::cout << "Hello, I am " << me << " out of " << size << "\n";

    if (me == 0) {
        Polynomial a(SIZE), b(SIZE);
        
        random_init(a);
        random_init(b);

        karatsubaMaster(a, b, size - 1);
    }
    else {
        karatsubaWorker(me, size - 1);
    }

    MPI_Finalize();
}