#include <iostream>
#include <cstdlib>
#include <string>
#include <sstream>
#include <thread>
#include <chrono>
#include <ctime>
#include <mpi.h>

#define VAR_COUNT 2

enum class Operations {
    LOCK,
    UNLOCK,
    READ,
    WRITE,
    DONE
};

enum class VariableCode {
    A,
    B
};

struct Variables {
    int a;
    int b;
};

void log(int id, std::string operation, std::string description) {

    printf("$ [%d] : [%s] => %s\n", id, operation.c_str(), description.c_str());
}

void lock(int me, VariableCode code) {

    std::stringstream ss;
    ss << "Send LOCK request for variable " << (int) code << "!";
    log(me, "LOCK", ss.str());

    MPI_Status status;
    bool locked = false;
    Operations operation = Operations::LOCK;
    while (!locked) {
        MPI_Send(&operation, sizeof(Operations), MPI_BYTE, 0, 1, MPI_COMM_WORLD);
        MPI_Send(&code, sizeof(VariableCode), MPI_BYTE, 0, 2, MPI_COMM_WORLD);
        MPI_Recv(&locked, sizeof(bool), MPI_BYTE, 0, 3, MPI_COMM_WORLD, &status);  

        if (!locked) {
            std::this_thread::sleep_for(std::chrono::milliseconds(rand() % 1000));
        }     
    }
}

void unlock(int me, VariableCode code) {
    
    std::stringstream ss;
    ss << "Send UNLOCK request for variable " << (int) code << "!";
    log(me, "UNLOCK", ss.str());

    Operations operation = Operations::UNLOCK;
    MPI_Send(&operation, sizeof(Operations), MPI_BYTE, 0, 1, MPI_COMM_WORLD);
    MPI_Send(&code, sizeof(VariableCode), MPI_BYTE, 0, 2, MPI_COMM_WORLD);
}

void read(int me, VariableCode code, void* data, unsigned int size) {

    std::stringstream ss;
    ss << "Send READ request for variable " << (int) code << "!";
    log(me, "READ", ss.str());

    MPI_Status status;
    Operations operation = Operations::READ;
    MPI_Send(&operation, sizeof(Operations), MPI_BYTE, 0, 1, MPI_COMM_WORLD);
    MPI_Send(&code, sizeof(VariableCode), MPI_BYTE, 0, 2, MPI_COMM_WORLD);
    MPI_Send(&size, sizeof(int), MPI_BYTE, 0, 3, MPI_COMM_WORLD);
    MPI_Recv(data, size, MPI_BYTE, 0, 4, MPI_COMM_WORLD, &status);      
}

void write(int me, VariableCode code, void* data, unsigned int size) {

    std::stringstream ss;
    ss << "Send WRITE request for variable " << (int) code << "!";
    log(me, "WRITE", ss.str());

    MPI_Status status;
    Operations operation = Operations::WRITE;
    MPI_Send(&operation, sizeof(Operations), MPI_BYTE, 0, 1, MPI_COMM_WORLD);
    MPI_Send(&code, sizeof(VariableCode), MPI_BYTE, 0, 2, MPI_COMM_WORLD);
    MPI_Send(&size, sizeof(int), MPI_BYTE, 0, 3, MPI_COMM_WORLD);
    MPI_Send(data, size, MPI_BYTE, 0, 4, MPI_COMM_WORLD);    
}

void done(int me) {

    log(me, "DONE", "Send DONE message to master!");

    Operations operation = Operations::DONE;
    MPI_Send(&operation, sizeof(Operations), MPI_BYTE, 0, 1, MPI_COMM_WORLD);
}

void process(int me) {

    Variables variables;

    lock(me, VariableCode::A);
    read(me, VariableCode::A, &variables.a, sizeof(int));

    variables.a += 1;

    write(me, VariableCode::A, &variables.a, sizeof(int));
    unlock(me, VariableCode::A);


    lock(me, VariableCode::B);
    read(me, VariableCode::B, &variables.b, sizeof(int));

    variables.b += variables.a;

    write(me, VariableCode::B, &variables.b, sizeof(int));
    unlock(me, VariableCode::B);

    done(me);
}

Variables* init_variables() {

    Variables *variables = new Variables;

    variables->a = 0;
    variables->b = 0;

    return variables;
}

int* init_ownership() {

    int *ownership = new int[VAR_COUNT];

    for (int index = 0; index < VAR_COUNT; ++index) {
        ownership[index] = 0;
    }

    return ownership;
}

void** init_variable_mappings(Variables* variables) {

    void** variable_mappings = new void*[VAR_COUNT];

    variable_mappings[(int) VariableCode::A] = &variables->a;
    variable_mappings[(int) VariableCode::B] = &variables->b;

    return variable_mappings;
}

void handle_lock(int child, int *ownership) {

    MPI_Status status;
    VariableCode variable;
    bool locked;

    MPI_Recv(&variable, sizeof(VariableCode), MPI_BYTE, child, 2, MPI_COMM_WORLD, &status);
    if (ownership[(int) variable] == 0 || ownership[(int) variable] == child) {
        ownership[(int) variable] = child;
        locked = true;
    }
    else {
        locked = false;
    }

    MPI_Send(&locked, sizeof(bool), MPI_BYTE, child, 3, MPI_COMM_WORLD);
}

void handle_unlock(int child, int *ownership) {

    MPI_Status status;
    VariableCode variable;

    MPI_Recv(&variable, sizeof(VariableCode), MPI_BYTE, child, 2, MPI_COMM_WORLD, &status);
    if (ownership[(int) variable] == child) {
        ownership[(int) variable] = 0;
    }
}

void handle_read(int child, int *ownership, void** variable_mappings) {

    MPI_Status status;
    VariableCode variable;
    int size;

    MPI_Recv(&variable, sizeof(VariableCode), MPI_BYTE, child, 2, MPI_COMM_WORLD, &status);
    MPI_Recv(&size, sizeof(int), MPI_BYTE, child, 3, MPI_COMM_WORLD, &status);
    
    void* data = variable_mappings[(int) variable];
    MPI_Send(data, size, MPI_BYTE, child, 4, MPI_COMM_WORLD);
}

void handle_write(int child, int *ownership, void** variable_mappings) {

    MPI_Status status;
    VariableCode variable;
    int size;
    void* data;

    MPI_Recv(&variable, sizeof(VariableCode), MPI_BYTE, child, 2, MPI_COMM_WORLD, &status);
    MPI_Recv(&size, sizeof(int), MPI_BYTE, child, 3, MPI_COMM_WORLD, &status);

    data = (void *) malloc(size);
    MPI_Recv(data, size, MPI_BYTE, child, 4, MPI_COMM_WORLD, &status);

    //free(variable_mappings[(int) variable]);
    variable_mappings[(int) variable] = data;
}

int main() {

    srand(time(0));

    MPI_Init(0, 0);

    int me, procs;

    MPI_Comm_rank(MPI_COMM_WORLD, &me);
    MPI_Comm_size(MPI_COMM_WORLD, &procs);

    log(me, "ALIVE", "Process started!");

    if (me == 0) {
        Variables *variables;
        int* ownership;
        void** variable_mappings;
        
        Operations operation;
        MPI_Status status;
        int sender;
        std::stringstream ss;

        variables = init_variables();
        ownership = init_ownership();
        variable_mappings = init_variable_mappings(variables);

        --procs;
        while (procs != 0) {
            MPI_Recv(&operation, sizeof(Operations), MPI_BYTE, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
            sender = status.MPI_SOURCE;

            switch (operation) {
                case Operations::LOCK:
                    ss << "Received LOCK request from " << sender << "!";
                    log(me, "RECEIVE", ss.str());

                    handle_lock(sender, ownership);
                    break;
                case Operations::UNLOCK:
                    ss << "Received UNLOCK request from " << sender << "!";
                    log(me, "RECEIVE", ss.str());

                    handle_unlock(sender, ownership);
                    break;
                case Operations::READ:
                    ss << "Received READ request from " << sender << "!";
                    log(me, "RECEIVE", ss.str());

                    handle_read(sender, ownership, variable_mappings);
                    break;
                case Operations::WRITE:
                    ss << "Received WRITE request from " << sender << "!";
                    log(me, "RECEIVE", ss.str());

                    handle_write(sender, ownership, variable_mappings);
                    break;
                case Operations::DONE:
                    ss << "Received DONE from " << sender << "!";
                    log(me, "RECEIVE", ss.str());
                    
                    --procs;
                    break;
                default:
                    ss << "Received unknown operation from " << sender << "!"; 
                    log(me, "UNKNOWN", ss.str());
                    break;
            }

            ss.str("");
        }

        void* var_a = variable_mappings[(int) VariableCode::A];
        int *a = (int *) var_a;
        std::cout << *a << "\n";

        void* var_b = variable_mappings[(int) VariableCode::B];
        int *b = (int *) var_b;
        std::cout << *b << "\n";

        free(variables);
        free(ownership);
        free(variable_mappings);
    }
    else {
        process(me);
    }

    MPI_Finalize();
}