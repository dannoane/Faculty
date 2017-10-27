#include "Sphere.hpp"
#include <iostream>
#include <float.h>

using namespace rt;

Intersection Sphere::getIntersection(const Line& line, double minDist, double maxDist) {
  Intersection in;

  Vector poi;
  poi = line.x0();

  this->center();

  // http://www.ambrsoft.com/TrigoCalc/Sphere/SpherLineIntersection_.htm
  double a = (line.x0().x() - line.dx().x()) * (line.x0().x() - line.dx().x()) + (line.x0().y() - line.dx().y()) * (line.x0().y() - line.dx().y()) + (line.x0().z() - line.dx().z()) * (line.x0().z() - line.dx().z());
  double b = 2 * ((line.x0().x() - line.dx().x()) * (line.dx().x() - this->_center.x()) + (line.x0().y() - line.dx().y()) * (line.dx().y() - this->_center.y()) + (line.x0().z() - line.dx().z()) * (line.dx().z() - this->_center.z()));
  double c = this->_center.x() * this->_center.x() + this->_center.y() * this->_center.y() + this->_center.z() * this->_center.z() + line.dx().x() * line.dx().x() + line.dx().y() * line.dx().y() + line.dx().z() * line.dx().z() - 2 * (this->_center.x() * line.dx().x() + this->_center.y() * line.dx().y() + this->_center.z() * line.dx().z()) - this->_radius*this->_radius;
  // condition for intersection
  double delta = b * b - 4 * a * c;
  double res;

  if (delta < 0){
    in =  Intersection(false, this, &line, DBL_MAX);
  }
  else {
    res = -((-b + sqrt(delta)) / 2 * a);
    in =  Intersection(true, this, &line, res);
  }

  return in;
}


const Vector Sphere::normal(const Vector& vec) const {
    Vector n = vec - _center;
    n.normalize();
    return n;
}
