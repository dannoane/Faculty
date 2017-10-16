#include "Sphere.hpp"

using namespace rt;

Intersection Sphere::getIntersection(const Line& line, double minDist, double maxDist) {
    Intersection in;

    // ADD CODE HERE

    return in;
}


const Vector Sphere::normal(const Vector& vec) const {
    Vector n = vec - _center;
    n.normalize();
    return n;
}
