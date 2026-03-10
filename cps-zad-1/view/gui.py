from tkinter import *
from tkinter import ttk


from .utils import pad_out, add_tab
from .gen_tab import make_gen_tab


def show_gui():
    root = Tk() 
    root.title("CPS Zadanie 1 - Kacper Majkowski i Tobiasz Kowalczyk")
    root.geometry("1024x1024")
    
    main_frame = ttk.Frame(root) 
    main_frame.grid(column=0, row=0, sticky=(N,S,E,W))

    nb = ttk.Notebook(main_frame)
    nb.grid(column=0, row=0, sticky=(N,S,E,W))

    load_tab = add_tab(nb, "Ładuj z pliku")
    gen_tab = make_gen_tab(nb)
    ops_tab = add_tab(nb, "Operacje")

    root.mainloop() 


    






    
