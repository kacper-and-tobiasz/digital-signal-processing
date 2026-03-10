from tkinter import ttk
from tkinter import *

from .utils import pad_out, add_tab

signal_types = [
    "(S0) sygnał stały",
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


def make_gen_tab(nb: ttk.Notebook):
    mainframe = add_tab(nb, "Generowanie sygnałów")

    add_selection_frame(mainframe)
    add_params_frame(mainframe)
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

def add_params_frame(mainframe):
    params_frame = LabelFrame(mainframe, text="Parametry sygnału")
    params_frame.grid(column=0, row=1)

    combobox = ttk.Combobox(params_frame, values=signal_types)
    combobox.current(0)
    combobox.grid(column=0, row=0)

    pad_out(params_frame)

def add_preview_Frame(mainframe):
    preview_frame = LabelFrame(mainframe, text="Podgląd sygnału")
    preview_frame.grid(column=1, row=0, rowspan=2)

    for child in preview_frame.winfo_children(): 
        child.grid_configure(padx=5, pady=5)

    pad_out(preview_frame)