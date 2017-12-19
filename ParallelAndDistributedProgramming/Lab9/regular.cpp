#include <iostream>
#include <cstdlib>
#include <ctime>
#include <mpi.h>

#define SIZE 100

class Polynomial {

private:
    int* polynomial;
    int len;

public:
    Polynomial(int len) {
        this->polynomial = new int[len];
        this->len = len;

        for (int index = 0; index < this->len; ++index) {
            this->polynomial[index] = 0;
        }
    }

    Polynomial(int *polynomial, int len) {
        this->polynomial = polynomial;
        this->len = len;
    }    

    int* getAll() {
        return polynomial;
    }

    int get(int index) {
        return this->polynomial[index];
    }

    void set(int index, int value) {
        this->polynomial[index] = value;
    }

    int size() {
        return this->len;
    }

    void print() {

        int index;

        for (index = 0; index < this->len; ++index) {
            std::cout << this->polynomial[index] << ' ';
        }
        std::cout << '\n';
    }
};

void random_init(Polynomial *p) {

    for (int index = 0; index < p->size(); ++index) {
        p->set(index, rand() % 5);
    }
}

void regular(Polynomial *a, Polynomial *b, int size) {

    int index, counter, start, end;
    Polynomial *c;
    MPI_Status status;

    c = new Polynomial(2 * SIZE);
    
    for (counter = 0; counter < size; ++counter) {
        start = counter * (SIZE / size);
        end = (counter + 1) * (SIZE / size) + (counter + 1 == size ? SIZE % size : 0);

        MPI_Send(&start, 1, MPI_INT, counter + 1, 1, MPI_COMM_WORLD);
        MPI_Send(&end, 1, MPI_INT, counter + 1, 2, MPI_COMM_WORLD);
        MPI_Send(a->getAll() + start, end - start, MPI_INT, counter + 1, 3, MPI_COMM_WORLD);
        MPI_Send(b->getAll(), SIZE, MPI_INT, counter + 1, 4, MPI_COMM_WORLD); 
    }

    std::cout << "SENT ALL\n";

    int *result = new int[2 * SIZE];
    for (counter = 0; counter < size; ++counter) {
        start = counter * (SIZE / size);
        end = (counter + 1) * (SIZE / size) + (counter + 1 == size ? SIZE % size : 0);
        std::cout << "RECV\n";
        MPI_Recv(result, 2 * SIZE, MPI_INT, counter + 1, 5, MPI_COMM_WORLD, &status);

        for (index = 0; index < 2 * SIZE; ++index) {
            c->set(index, c->get(index) + result[index]);
        }
    }

    a->print();
    b->print();
    c->print();
}

void regularWorker(int me) {

    int start, end, indexA, indexB, parent;
    int *a, *b;
    MPI_Status status;
    Polynomial *result;

    a = new int[SIZE];
    b = new int[SIZE];
    result = new Polynomial(2 * SIZE);

    MPI_Recv(&start, 1, MPI_INT, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
    MPI_Recv(&end, 1, MPI_INT, MPI_ANY_SOURCE, 2, MPI_COMM_WORLD, &status);
    MPI_Recv(a + start, end - start, MPI_INT, MPI_ANY_SOURCE, 3, MPI_COMM_WORLD, &status);
    MPI_Recv(b, SIZE, MPI_INT, MPI_ANY_SOURCE, 4, MPI_COMM_WORLD, &status);

    parent = status.MPI_SOURCE;    

    for (indexA = start; indexA < end; ++indexA) {
        for (int indexB = 0; indexB < SIZE; ++indexB) {
            result->set(indexA + indexB, result->get(indexA + indexB) + a[indexA] * b[indexB]);
        }
    }

    MPI_Ssend(result->getAll(), 2 * SIZE, MPI_INT, parent, 5, MPI_COMM_WORLD);
}

int main() {

    srand((unsigned int) time(0));
    
    MPI_Init(0, 0);

    int me, size;

    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &me);

    std::cout << "Hello, I am " << me << " out of " << size << "\n";

    if (me == 0) {
        Polynomial *a, *b;
        
        a = new Polynomial(SIZE);
        b = new Polynomial(SIZE);

        random_init(a);
        random_init(b);

        regular(a, b, size - 1);
    }
    else {
        regularWorker(me);
    }

    MPI_Finalize();
}