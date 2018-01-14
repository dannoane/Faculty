#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <chrono>
#include <mpi.h>
#include "lodepng.h"

class Color {

public:
    unsigned char red, green, blue, alpha;

    Color() {

    }

    Color(unsigned char red, unsigned char green, unsigned char blue, unsigned char alpha) {
        this->red = red;
        this->green = green;
        this->blue = blue;
        this->alpha = alpha;
    }
};




class Image {
    
private:
    std::string filename;
    std::vector<Color> image;
    unsigned int width, height;

public:
    Image(std::string filename) {
        this->filename = filename;
    }

    Image(unsigned int width, unsigned int height, Color* data) {
        this->width = width;
        this->height = height;

        this->image.assign(data, data + this->width * this->height);
    }

    void read() {

        std::vector<unsigned char> data;
        unsigned error = lodepng::decode(data, this->width, this->height, this->filename);

        if (error) {
            std::cout << "decoder error " << error << ": " << lodepng_error_text(error) << std::endl;
        }

        for (int index = 0; index < data.size(); index += 4) {
            Color *color = new Color(
                data[index], data[index + 1], data[index + 2], data[index + 3]
            );
            this->image.push_back(*color);
        }
    }

    void write() {

        std::vector<unsigned char> data;
        for (int index = 0; index < this->image.size(); ++index) {
            data.push_back(this->image[index].red);
            data.push_back(this->image[index].green);
            data.push_back(this->image[index].blue);
            data.push_back(this->image[index].alpha);
        }        
        unsigned error = lodepng::encode("blurred" + this->filename, data, this->width, this->height);

        if (error) {
            std::cout << "encoder error " << error << ": "<< lodepng_error_text(error) << std::endl;
        }
    }

    Color getRGB(int index) {

        return this->image[index];
    }

    void setColor(int index, Color color) {

        this->image[index] = color;
    }

    unsigned int getWidth() {
        return this->width;
    }

    unsigned int getHeight() {
        return this->height;
    } 

    unsigned int size() {
        return this->width * this->height;
    }

    std::vector<Color> getImage() {
        return this->image;
    }
};




class MotionBlur {

private:
    static const unsigned int filterWidth = 9;
    static const unsigned int filterHeight = 9;
    static const float filter[filterWidth][filterHeight];
    static const float factor;
    static const float bias;

public:

    static void apply(Image *image, long start, long end) {

        float red, green, blue;
        long imageX, imageY;
        Color color, newColor;

        long x, y;
        for (long index = start; index < end; ++index) {
            x = index % image->getWidth();
            y = index / image->getWidth();

            red = green = blue = 0.0f;

            for (long filterY = 0; filterY < filterHeight; ++filterY) {
                for (long filterX = 0; filterX < filterWidth; ++filterX) {
                    imageX = (x - filterWidth / 2 + filterX + image->getWidth()) % image->getWidth();
                    imageY = (y - filterHeight / 2 + filterY + image->getHeight()) % image->getHeight();

                    color = image->getRGB(imageY * image->getWidth() + imageX);
                    red += color.red * filter[filterY][filterX];
                    green += color.green * filter[filterY][filterX];
                    blue += color.blue * filter[filterY][filterX];
                }
            }

            newColor.red = std::min(std::max(int(factor * red + bias), 0), 255);
            newColor.green = std::min(std::max(int(factor * green + bias), 0), 255);
            newColor.blue = std::min(std::max(int(factor * blue + bias), 0), 255);
            newColor.alpha = color.alpha;
            
            image->setColor(y * image->getWidth() + x, newColor);
        }
    }
};

const float MotionBlur::filter[MotionBlur::filterHeight][MotionBlur::filterWidth] = {
    {1, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 1, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 1, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 1, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 1, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 1, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 1, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 1, 0},
    {0, 0, 0, 0, 0, 0, 0, 0, 1}
};

const float MotionBlur::factor = 1.0f / 9.0f;
const float MotionBlur::bias = 0.0f;




void filterMaster(Image *image, int nrProcs) {

    if (nrProcs > 0) {
        unsigned int width, height;
        long start, end;

        width = image->getWidth();
        height = image->getHeight();

        for (int i = 0; i < nrProcs; ++i) {
            start = i * (int) (image->size() / nrProcs);
            end = (i + 1) * (int) (image->size() / nrProcs) + ((i + 1) == nrProcs ? image->size() % nrProcs : 0);

            MPI_Send(&start, 1, MPI_LONG, i + 1, 1, MPI_COMM_WORLD);
            MPI_Send(&end, 1, MPI_LONG, i + 1, 2, MPI_COMM_WORLD);
            MPI_Send(&width, 1, MPI_INT, i + 1, 3, MPI_COMM_WORLD);
            MPI_Send(&height, 1, MPI_INT, i + 1, 4, MPI_COMM_WORLD);
            MPI_Send(image->getImage().data(), image->getImage().size() * sizeof(Color), MPI_BYTE, i + 1, 5, MPI_COMM_WORLD);
        }

        MPI_Status status;
        Color *chunk;

        for (int i = 0; i < nrProcs; ++i) {
            start = i * (int) (image->size() / nrProcs);
            end = (i + 1) * (int) (image->size() / nrProcs) + ((i + 1) == nrProcs ? image->size() % nrProcs : 0);

            chunk = new Color[end - start];

            MPI_Recv(chunk, (end - start) * sizeof(Color), MPI_BYTE, i + 1, 6, MPI_COMM_WORLD, &status);
            for (long index = start; index < end; ++index) {
                image->setColor(index, chunk[index - start]);
            }

            free(chunk);
        }

        image->write();
    }
}




void filterWorker(int me) {

    long start, end;
    unsigned int width, height;
    Color* data;
    Image *image;
    MPI_Status status;

    MPI_Recv(&start, 1, MPI_LONG, MPI_ANY_SOURCE, 1, MPI_COMM_WORLD, &status);
    MPI_Recv(&end, 1, MPI_LONG, MPI_ANY_SOURCE, 2, MPI_COMM_WORLD, &status);
    MPI_Recv(&width, 1, MPI_INT, MPI_ANY_SOURCE, 3, MPI_COMM_WORLD, &status);
    MPI_Recv(&height, 1, MPI_INT, MPI_ANY_SOURCE, 4, MPI_COMM_WORLD, &status);
    data = new Color[width * height];
    MPI_Recv(data, (width * height) * sizeof(Color), MPI_BYTE, MPI_ANY_SOURCE, 5, MPI_COMM_WORLD, &status);

    image = new Image(width, height, data);    
    MotionBlur::apply(image, start, end);

    MPI_Send(image->getImage().data() + start, (end - start) * sizeof(Color), MPI_BYTE, status.MPI_SOURCE, 6, MPI_COMM_WORLD);
}




int main() {

    MPI_Init(0, 0);
    int me;
    int nrProcs;
    MPI_Comm_size(MPI_COMM_WORLD, &nrProcs);
    MPI_Comm_rank(MPI_COMM_WORLD, &me);

    if (me == 0) {
        Image *image;

        image = new Image("test.png");
        image->read();

        auto started = std::chrono::high_resolution_clock::now();
        filterMaster(image, nrProcs - 1);
        auto done = std::chrono::high_resolution_clock::now();
        std::cout << "Took " << std::chrono::duration_cast<std::chrono::milliseconds>(done-started).count() << " ms to complete" << std::endl;
    }
    else {
        filterWorker(me);
    }

    MPI_Finalize();
}