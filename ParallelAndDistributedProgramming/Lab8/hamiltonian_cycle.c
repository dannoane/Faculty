#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <stdatomic.h>

#define SIZE 5
#define THREADS 100

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

struct Graph {
    unsigned int **adj_mat;
    unsigned int size;
};

struct Data {
    struct Shared *shared_data;
    struct Private *private_data;
};

struct Shared {
    struct Graph *graph;
    atomic_uint threads;
    atomic_int found;
};

struct Private {
    unsigned int *circuit;
    unsigned int length;
};

void init_graph(struct Graph **graph) {

    unsigned int i, j;

    *graph = (struct Graph *) malloc(sizeof(struct Graph));
    (*graph)->size = SIZE;
    (*graph)->adj_mat = (unsigned int **) malloc(SIZE * sizeof(unsigned int *));
    for (i = 0; i < SIZE; ++i) {
        (*graph)->adj_mat[i] = (unsigned int *) malloc(SIZE * sizeof(unsigned int));
        for (j = 0; j < SIZE; ++j) {
            (*graph)->adj_mat[i][j] = 1;
        }
    }
}

void dest_graph(struct Graph *graph) {

    unsigned int i;

    for (i = 0; i < SIZE; ++i) {
        free(graph->adj_mat[i]);
    }
    free(graph->adj_mat);
}

void print_circuit(unsigned int *circuit, unsigned int len) {

    unsigned int i;
    for (i = 0; i < len; ++i) {
        printf("%d ", circuit[i]);
    }
    putchar('\n');
}

unsigned int find(unsigned int *circuit, unsigned int length, unsigned int v) {

    unsigned int i;
    for (i = 0; i < SIZE; ++i) {
        if (circuit[i] == v) {
            return 1;
        }
    }

    return 0;
}

void copy_data(struct Data *dest, struct Data *src) {

    int k;

    dest->shared_data = src->shared_data;
    dest->private_data = (struct Private *) malloc(sizeof(struct Private));
    dest->private_data->length = src->private_data->length;
    dest->private_data->circuit = (unsigned int *) malloc(SIZE * sizeof(unsigned int));
    for (k = 0; k < src->private_data->length; ++k) {
        dest->private_data->circuit[k] = src->private_data->circuit[k];
    }
}

void* hamiltonian_circuit_recursive(void *_data) {

    struct Data *data = (struct Data *) _data;

    if (atomic_load(&data->shared_data->found) != 0) {
        return NULL;
    }

    if (data->private_data->length == SIZE) {
        atomic_store(&data->shared_data->found, 1);
        print_circuit(data->private_data->circuit, data->private_data->length);
        return NULL;
    }

    pthread_t *threads = (pthread_t *) malloc(atomic_load(&data->shared_data->threads) * sizeof(pthread_t));
    int thread_number = 0;

    unsigned int i;
    for (i = 1; i < SIZE; ++i) {
        if (!find(data->private_data->circuit, data->private_data->length, i)
            && data->shared_data->graph->adj_mat[data->private_data->circuit[data->private_data->length - 1]][i] == 1
            && (data->private_data->length < SIZE - 1 
            || (data->shared_data->graph->adj_mat[0][i] == 1 && i < data->private_data->circuit[1]))) {
                
                data->private_data->circuit[data->private_data->length] = i;
                ++data->private_data->length;

                if (atomic_load(&data->shared_data->threads) > 0) {
                    atomic_fetch_sub(&data->shared_data->threads, 1);
                    struct Data *new_data = (struct Data *) malloc(sizeof(struct Data));
                    copy_data(new_data, data);
                    pthread_create(&threads[thread_number++], NULL, hamiltonian_circuit_recursive, new_data);
                    atomic_fetch_add(&data->shared_data->threads, 1);
                }
                else {
                    hamiltonian_circuit_recursive(data);
                }

                --data->private_data->length;
        }
    }

    for (i = 0; i < thread_number; ++i) {
        pthread_join(threads[i], NULL);
    }
    free(threads);

    return NULL;
}

void hamiltonian_circuit() {

    unsigned int *circuit;
    circuit = (unsigned int *) malloc(SIZE * sizeof(unsigned int));
    circuit[0] = 0;

    struct Data *data = (struct Data *) malloc(sizeof(struct Data));
    data->shared_data = (struct Shared *) malloc(sizeof(struct Shared));
    data->private_data = (struct Private *) malloc(sizeof(struct Private));
    init_graph(&data->shared_data->graph);
    data->private_data->circuit = circuit;
    data->private_data->length = 1;
    atomic_store(&data->shared_data->threads, THREADS - 1);
    atomic_store(&data->shared_data->found, 0);

    pthread_t thread;
    pthread_create(&thread, NULL, hamiltonian_circuit_recursive, data);
    pthread_join(thread, NULL);

    dest_graph(data->shared_data->graph);
    free(data);
    free(circuit);
}

int main() {

    hamiltonian_circuit();

    return 0;
}