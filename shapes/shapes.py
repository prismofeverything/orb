from solid import *
from solid.utils import *

import numpy as np

def position():
    np.random.multivariate_normal(np.zeros((3, )), np.array([[1, 2, 3], [2, 3, 1], [3, 1, 2]]))

orb = sphere(13)

block = cube(8)


def throw():
    to = position()
    return translate(to)

def scatter(n):
    mass = None

    for index in range(n):
        to = throw()
        size = 1 # np.absolute(np.random.normal())
        orb = sphere(size)
        to(orb)

        if not mass:
            mass = orb
        else:
            mass += orb

    return mass
