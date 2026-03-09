import math

import numpy as np


def base_sinus(A, T, t1, d, f):
    t = np.arange(0, d+1, 1/f)+ t1
    return A * math.sin((2 * math.pi/T) * (t - t1))

def sinus_rectified(A, T, t1, d, f):
    t = np.arange(0, d+1, 1/f)+ t1
    return 1/2 * A *(math.sin(2 * math.pi/T * (t - t1)) + abs(math.sin(2 * math.pi/T * (t - t1))))

def sinus_full_rectified(A, T, t1, d, f):
    t = np.arange(0, d+1, 1/f)+ t1
    return A * abs(math.sin(2 * math.pi/T * (t - t1)))

def rectangular(A, T, t1, d, kw, f):
    t = np.arange(0, d+1, 1/f)+ t1
    if (t - t1) % T < kw * T:
        return A
    else:
        return 0

def rectangular_simetric(A, T, t1, d, kw, f):
    t = np.arange(0, d+1, 1/f)+ t1
    if (t - t1) % T < kw * T:
        return A
    else:
        return -A

def triangular(A, T, t1, d, kw, f):
    t = np.arange(0, d+1, 1/f)+ t1
    if (t - t1) % T < kw * T:
        return A/(kw * T) * ((t - t1) % T)
    else:
        return -A/T(1 - kw) * ((t - t1) % T) + A / 1 - kw

def jump(A, t1, d, f, ts):
    t = np.arange(0, d+1, 1/f)+ t1
    if t < ts:
        return 0
    elif t == ts:
        return A / 2
    else:
        return A

def unit_impuls(A, ns, n1, l, f):
    n = np.arange(n1, n1 + l)
    t = n / f
    x = np.zeros(l)
    for i in range(l):
        n = n1 + i
        if n == ns:
            x[i] = A
    return t, x

def impulse_noise(A, t1, d, f, p):
    t = np.arange(0, d + 1, 1 / f) + t1
    N = int(d * f)
    x = np.zeros(N)
    for i in range(N):
        if np.random.rand() < p:
            x[i] = A
    return x

def uniform_noise(A, t1, d, f):
    t = np.arange(0, d + 1, 1 / f) + t1
    x = np.random.uniform(-A, A, len(t))
    return x

def gen_gaussian_noise(A, t1, d, f):
    t = np.arange(0, d, 1/f) + t1
    x = A * np.random.normal(0, 1, len(t))
    return t, x
