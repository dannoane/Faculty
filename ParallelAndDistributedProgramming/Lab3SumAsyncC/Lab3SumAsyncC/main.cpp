//
//  main.cpp
//  Lab3SumAsyncC
//
//  Created by Noane Dan on 28/10/2017.
//  Copyright © 2017 Noane Dan. All rights reserved.
//

#include <future>
#include <vector>
#include <iostream>
#include <cstdlib>
#include <ctime>
#include <chrono>

class Matrix {
    
private:
    int **matrix;
    int rows;
    int cols;
    
public:
    Matrix(int rows, int cols) {
        this->rows = rows;
        this->cols = cols;
        
        this->matrix = new int*[rows];
        for (int i = 0; i < rows; ++i)
            this->matrix[i] = new int[cols];
    }
    
    int getRows() {
        return this->rows;
    }
    
    int getCols() {
        return this->cols;
    }
    
    int size() {
        return this->rows * this->cols;
    }
    
    int get(int row, int col) {
        return this->matrix[row][col];
    }
    
    void set(int row, int col, int val) {
        this->matrix[row][col] = val;
    }
    
    void randInit() {
        
        for (int i = 0; i < this->rows; ++i) {
            for (int j = 0; j < this->cols; ++j) {
                this->matrix[i][j] = rand() % 20;
            }
        }
    }
    
    void print() {
        
        for (int i = 0; i < this->rows; ++i) {
            for (int j = 0; j < this->cols; ++j) {
                std::cout << this->matrix[i][j] << " ";
            }
            std::cout << std::endl;
        }
    }
};

long sum(Matrix *a, Matrix *b, Matrix *c, int start, int end) {
    
    for (int index = start; index < end; ++index) {
        int row = index / c->getCols();
        int col = index % c->getCols();
        
        c->set(row, col, a->get(row, col) + b->get(row, col));
    }
    
    return 1;
}

int main(int argc, const char * argv[]) {
    
    srand((unsigned int) time(0));
    
    int rows, cols;
    rows = cols = 100;
    
    Matrix *a, *b, *c;
    a = new Matrix(rows, cols);
    b = new Matrix(rows, cols);
    c = new Matrix(rows, cols);
    
    a->randInit();
    b->randInit();
    
    int num_jobs = 200;
    std::vector<std::future<long>> jobs;
    
    auto started = std::chrono::high_resolution_clock::now();
    for (int counter = 0; counter < num_jobs; ++counter) {
        int start = counter * (c->size() / num_jobs);
        int end = (counter + 1) * (c->size() / num_jobs) + (counter + 1 == num_jobs ? c->size() % num_jobs : 0);
       
        jobs.push_back(std::async(sum, a, b, c, start, end));
    }
    
    for (int i = 0; i < jobs.size(); ++i) {
        jobs[i].get();
    }
    auto done = std::chrono::high_resolution_clock::now();
    std::cout << "Took " << std::chrono::duration_cast<std::chrono::milliseconds>(done-started).count() << " ms to complete using " << num_jobs << " async jobs" << std::endl;
    
//    a->print();
//    b->print();
//    c->print();
    
    return 0;
}
