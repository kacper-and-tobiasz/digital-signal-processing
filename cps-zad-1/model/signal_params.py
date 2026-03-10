from tkinter import *

class SignalParams:
    def __init__(self):
        self.amplitude_var = DoubleVar(value=1.0)
        self.start_time_var = DoubleVar(value=0.0)
        self.duration_var = DoubleVar(value=5.0)
        self.frequency_var = DoubleVar(value=1.0)
        self.period_var = DoubleVar(value=1.0)
        self.duty_cycle_var = DoubleVar(value=0.5)
        self.mean_var = DoubleVar(value=0.0)
        self.std_var = DoubleVar(value=1.0)