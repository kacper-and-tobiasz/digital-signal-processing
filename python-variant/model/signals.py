import numpy as np


def base_sinus(A, T, t1, d, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    return t, A * np.sin((2 * np.pi / T) * (t - t1))


# jednopolowkowe
def sinus_rectified(A, T, t1, d, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    return t, 0.5 * A * (np.sin(2 * np.pi / T * (t - t1)) + np.abs(np.sin(2 * np.pi / T * (t - t1))))


# dwupolowkowe
def sinus_full_rectified(A, T, t1, d, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    return t, A * np.abs(np.sin(2 * np.pi / T * (t - t1)))


def rectangular(A, T, t1, d, kw, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    x = np.where((t - t1) % T < kw * T, A, 0.0)
    return t, x


def rectangular_simetric(A, T, t1, d, kw, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    x = np.where((t - t1) % T < kw * T, A, -A)
    return t, x


def triangular(A, T, t1, d, kw, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    phase = (t - t1) % T
    rising  = A / (kw * T) * phase
    falling = -A / ((1 - kw) * T) * phase + A / (1 - kw)
    x = np.where(phase < kw * T, rising, falling)
    return t, x


def jump(A, t1, d, f, ts):
    t = np.arange(0, d + 1/f, 1/f) + t1
    x = np.where(t < ts, 0.0, np.where(t == ts, A / 2, A))
    return t, x


def unit_impuls(A, ns, n1, l, f):
    n = np.arange(n1, n1 + l)
    t = n / f
    x = np.zeros(l)
    x[ns - n1] = A
    return t, x


def impulse_noise(A, t1, d, f, p):
    t = np.arange(0, d + 1/f, 1/f) + t1
    x = np.where(np.random.rand(len(t)) < p, A, 0.0)
    return t, x


def uniform_noise(A, t1, d, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    x = np.random.uniform(-A, A, len(t))
    return t, x


def gen_gaussian_noise(A, t1, d, f):
    t = np.arange(0, d + 1/f, 1/f) + t1
    x = A * np.random.normal(0, 1, len(t))
    return t, x