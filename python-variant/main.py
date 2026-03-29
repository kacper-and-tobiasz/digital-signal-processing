import math
from tkinter import Tk
from view.gui import show_gui
from model.storage import Storage

def main():
    gui_root = Tk()
    storage = Storage()
    show_gui(gui_root, storage)


if __name__ == "__main__":
    main()

