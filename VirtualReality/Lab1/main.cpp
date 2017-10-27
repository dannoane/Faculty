#include <cmath>
#include <iostream>
#include <string>

#include "Vector.hpp"
#include "Line.hpp"
#include "Geometry.hpp"
#include "Sphere.hpp"
#include "Image.hpp"
#include "Color.hpp"
#include "Intersection.hpp"
#include "Material.hpp"
#include "stdio.h"

#include "Scene.hpp"

using namespace std;
using namespace rt;

double imageToViewPlane(int n, int imgSize, double viewPlaneSize) {

  double u = (double) n * viewPlaneSize / (double) imgSize;
  u -= viewPlaneSize / 2;

  return u;
}

const Intersection findFirstIntersection(const Line& ray, double minDist, double maxDist) {

    Intersection intersection;

    for (int i = 0; i < geometryCount; ++i) {
        Intersection in = scene[i]->getIntersection(ray, minDist, maxDist);

        if (in.valid()) {
            if (!intersection.valid()) {
                intersection = in;
            }
            else if (in.t() < intersection.t()) {
                intersection = in;
            }
        }
    }

    return intersection;
}

int main() {

    Vector viewPoint(0, 0, 0);
    Vector viewDirection(0, 0, 1);
    Vector viewUp(0, -1, 0);

    Vector x0, x1;

    double viewPlaneDist = 512;
    double viewPlaneWidth = 1024;
    double viewPlaneHeight = 768;

    int imageWidth = 1024;
    int imageHeight = 768;
    int i, j, k;
    Vector viewParallel = viewUp ^ viewDirection;

    viewDirection.normalize();
    viewUp.normalize();
    viewParallel.normalize();

    Image image(imageWidth, imageHeight);

    Color *c = new Color(0, 0, 0);

    Vector n, t, e, r;

    for (i = 0; i < imageWidth; ++i) {
      for (j = 0; j < imageHeight; ++j) {
        // background color
        image.setPixel(i, j, Color(0.3, 0.3, 0.3));

        // camera
        x0 = viewPoint;
        // point in space
        x1 = viewPoint + viewDirection * viewPlaneDist + viewUp
          * imageToViewPlane(j, imageHeight, viewPlaneHeight) + viewParallel
          * imageToViewPlane(i, imageWidth, viewPlaneWidth);

        // line from viewpoint to point in space
        Line *line = new Line(x0, x1, true);

        Intersection intersection = findFirstIntersection(*line, 0.25 , 0.25);
        if (intersection.valid()) {
          *c = intersection.geometry()->material().ambient();

          for (k = 0; k < 2; ++k) {
            *c *= lights[k]->ambient();

            // normal to the surface
            n = intersection.vec() - ((Sphere *)intersection.geometry())->center();
            n.normalize();

            // vector from intersection to light
            t = lights[k]->position() - intersection.vec();
            t.normalize();

            if (n * t > 0) {
              *c += intersection.geometry()->material().diffuse()*lights[k]->diffuse()*(n * t);
            }

            // vector from intersection point to camera
            e = viewPoint - intersection.vec();
            e.normalize();

            // reflection vector
            r = n * 2 * (n * t) - t;
            r.normalize();

            if(e * r > 0) {
              *c += intersection.geometry()->material().specular() * lights[k]->specular() * pow(e * r, intersection.geometry()->material().shininess());
            }
            *c *= lights[k]->intensity();
          }

          image.setPixel(i, j, *c);
        }
      }
    }

    image.store("scene.png");

    for (int i = 0; i < geometryCount; i++) {
        delete scene[i];
    }

    return 0;
}
