from tkinter import ttk
from tkinter import *

from .utils import pad_out, add_tab
from model.storage import *

signal_types = [
    "(S0) brak sygnału",
    "(S1) szum o rozkładzie jednostajnym",
    "(S2) szum gaussowski",
    "(S3) sygnał sinusoidalny",
    "(S4) sygnał sinusoidalny wyprostowany jednopołówkowo",
    "(S5) sygnał sinusoidalny wyprostowany dwupołówkowo",
    "(S6) sygnał prostokątny",
    "(S7) sygnał prostokątny symetryczny",
    "(S8) sygnał trójkątny",
    "(S9) skok jednostkowy",
    "(S10) impuls jednostkowy",
    "(S11) szum impulsowy"
]


def make_gen_tab(nb: ttk.Notebook, storage: Storage):
    mainframe = add_tab(nb, "Generowanie sygnałów")

    add_selection_frame(mainframe)
    add_params_frame(mainframe, storage)
    add_preview_Frame(mainframe)


def add_selection_frame(mainframe):
    selection_frame = LabelFrame(mainframe, text="Wybór sygnałów")
    selection_frame.grid(column=0, row=0)

    ttk.Button(selection_frame, text="Nowy sygnał").grid(column=0, row=0)
    ttk.Button(selection_frame, text="Usuń sygnał").grid(column=1, row=0)

    signal_name = StringVar()
    ttk.Label(selection_frame, text="Nazwa sygnału: ").grid(column=0, row=1)
    ttk.Entry(selection_frame, textvariable=signal_name).grid(column=1, row=1)

    selected_signal = IntVar()
    ttk.Label(selection_frame, text="Wybierz sygnał: ").grid(column=0, row=2)
    ttk.Combobox(selection_frame).grid(column=1, row=2)
    
    pad_out(selection_frame)

def add_params_frame(mainframe, storage: Storage):
    params_frame = LabelFrame(mainframe, text="Parametry sygnału")
    params_frame.grid(column=0, row=1)

    ttk.Label(params_frame, text="Rodzaj sygnału: ").grid(column=0, row=0, sticky="w")
    combobox = ttk.Combobox(params_frame, values=signal_types)
    combobox.current(0)
    combobox.grid(column=1, row=0)
    
    p = storage.current_params

    ttk.Label(params_frame, text="Amplituda (A):").grid(column=0, row=1, sticky="w")
    ttk.Label(params_frame, text="Czas początkowy (t_1) [s]:").grid(column=0, row=2, sticky="w")
    ttk.Label(params_frame, text="Czas trwania sygnału (d) [s]:").grid(column=0, row=3, sticky="w")
    ttk.Label(params_frame, text="Częstotliwość sygnału (f_s) [Hz]:").grid(column=0, row=4, sticky="w")
    ttk.Label(params_frame, text="Okres podstawowy (T) [s]:").grid(column=0, row=5, sticky="w")
    ttk.Label(params_frame, text="Wsp. wypełnienia kwadr. (k_w):").grid(column=0, row=6, sticky="w")
    ttk.Label(params_frame, text="Średnia (μ):").grid(column=0, row=7, sticky="w")
    ttk.Label(params_frame, text="Odchylenie standardowe (σ):").grid(column=0, row=8, sticky="w")

    ttk.Entry(params_frame, textvariable=p.amplitude_var).grid(column=1, row=1)
    ttk.Entry(params_frame, textvariable=p.start_time_var).grid(column=1, row=2)
    ttk.Entry(params_frame, textvariable=p.duration_var).grid(column=1, row=3)
    ttk.Entry(params_frame, textvariable=p.frequency_var).grid(column=1, row=4)
    ttk.Entry(params_frame, textvariable=p.period_var).grid(column=1, row=5)
    ttk.Entry(params_frame, textvariable=p.duty_cycle_var).grid(column=1, row=6)
    ttk.Entry(params_frame, textvariable=p.mean_var).grid(column=1, row=7)
    ttk.Entry(params_frame, textvariable=p.std_var).grid(column=1, row=8)

    
    ttk.Scale(params_frame, from_=0, to=10, orient="horizontal", variable=p.amplitude_var).grid(column=2, row=1, sticky="ew")
    ttk.Scale(params_frame, from_=0, to=10, orient="horizontal", variable=p.start_time_var).grid(column=2, row=2, sticky="ew")
    ttk.Scale(params_frame, from_=0, to=10, orient="horizontal", variable=p.duration_var).grid(column=2, row=3, sticky="ew")
    ttk.Scale(params_frame, from_=0, to=100, orient="horizontal", variable=p.frequency_var).grid(column=2, row=4, sticky="ew")
    ttk.Scale(params_frame, from_=0, to=10, orient="horizontal", variable=p.period_var).grid(column=2, row=5, sticky="ew")
    ttk.Scale(params_frame, from_=0, to=1, orient="horizontal", variable=p.duty_cycle_var).grid(column=2, row=6, sticky="ew")
    ttk.Scale(params_frame, from_=-5,to=5, orient="horizontal", variable=p.mean_var).grid(column=2, row=7, sticky="ew")
    ttk.Scale(params_frame, from_=0, to=5, orient="horizontal", variable=p.std_var).grid(column=2, row=8, sticky="ew")

    pad_out(params_frame)

    sample_rate = DoubleVar()
    ttk.Label(params_frame, text="Częstotliwość próbkowania (f) [Hz]:")\
        .grid(column=0, row=9, sticky="w", pady=15, padx=5)
    ttk.Entry(params_frame, textvariable=sample_rate)\
        .grid(column=1, row=9, pady=15, padx=5)
    ttk.Scale(params_frame, from_=2, to=44100, orient="horizontal", variable=sample_rate)\
        .grid(column=2, row=9, padx=5, pady=15, sticky="ew")
    

def add_preview_Frame(mainframe):
    preview_frame = LabelFrame(mainframe, text="Podgląd sygnału")
    preview_frame.grid(column=1, row=0, rowspan=2)

    for child in preview_frame.winfo_children(): 
        child.grid_configure(padx=5, pady=5)

    pad_out(preview_frame)