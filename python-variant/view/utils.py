from tkinter import ttk
from tkinter import *

def add_tab(nb: ttk.Notebook, name) -> Frame:
    gen_tab = Frame(nb)
    gen_tab.grid(column=0, row=0, sticky=(N,S,E,W))
    nb.add(gen_tab, text=name)
    return gen_tab

def pad_out(widget):
    for child in widget.winfo_children(): 
        child.grid_configure(padx=5, pady=5)
