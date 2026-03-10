from tkinter import *
from .signal_params import SignalParams

class Storage:
    def __init__(self):
        self.signals = Variable(value=[])
        self.signal_names = Variable(value=[])

        self.current_params = SignalParams()


