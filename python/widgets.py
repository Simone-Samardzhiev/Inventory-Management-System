import functions
from tkinter import *


class ChangeItemButton(Button):
    def __init__(self, master=None, function=None, **kwargs):
        super().__init__(master, kwargs)
        self.config(text="Change item", command=function)
        self.grid(row=4, column=2)


class ChangeItemEntry(Entry):
    default_text = "Name:Price:Quantity"

    def __init__(self, master=None, **kwargs):
        super().__init__(master, kwargs)
        self.grid(row=4, column=1)

        self.insert(0, self.default_text)
        self.bind("<FocusIn>", self.on_focus)
        self.bind("<FocusOut>", self.on_focus_out)
        self.configure(fg="grey")

    def on_focus(self, event):
        if self.get() == self.default_text:
            self.delete(0, END)
            self.configure(fg="black")

    def on_focus_out(self, event):
        if not self.get():
            self.insert(0, self.default_text)
            self.configure(fg="grey")


class NewItemButton(Button):
    def __init__(self, master=None, function=None, **kwargs):
        super().__init__(master, kwargs)
        self.config(text="write new item", command=function)
        self.grid(row=2, column=2)


class NewItemEntry(Entry):
    default_text = "Name,Price,Quantity"

    def __init__(self, master=None, **kwargs):
        super().__init__(master, kwargs)
        self.grid(row=2, column=1)

        self.insert(0, self.default_text)
        self.bind("<FocusIn>", self.on_focus)
        self.bind("<FocusOut>", self.on_focus_out)
        self.configure(fg="grey")

    def on_focus(self, event):
        if self.get() == self.default_text:
            self.delete(0, END)
            self.configure(fg="black")

    def on_focus_out(self, event):
        if not self.get():
            self.insert(0, self.default_text)
            self.configure(fg="grey")


class DeleteButton(Button):
    def __init__(self, master=None, function=None, **kwargs):
        super().__init__(master, kwargs)
        self.config(text="Delete item", command=function)
        self.grid(row=0, column=2)


class DeleteEntry(Entry):
    default_text = "Enter the name"

    def __init__(self, master=None, **kwargs):
        super().__init__(master, kwargs)
        self.grid(row=0, column=1)

        self.insert(0, self.default_text)
        self.bind("<FocusIn>", self.on_focus)
        self.bind("<FocusOut>", self.on_focus_out)
        self.configure(fg="grey")

    def on_focus(self, event):
        if self.get() == self.default_text:
            self.delete(0, END)
            self.configure(fg="black")

    def on_focus_out(self, event):
        if not self.get():
            self.insert(0, self.default_text)
            self.configure(fg="grey")


class FunctionArea(Frame):
    def __init__(self, master=None, text_area=None, **kwargs):
        super().__init__(master, kwargs)
        self.pack(side=RIGHT, fill=BOTH, expand=True)

        self.text_area = text_area

        Label(self, text="Enter the name of the item you want to delete :").grid(row=0, column=0)
        self.delete_entry = DeleteEntry(self)
        self.delete_button = DeleteButton(self, function=self.delete_item)
        self.delete_error = Label(self, fg="red")
        self.delete_error.grid(row=1, column=1)

        Label(self, text="Enter new item like this Name,Price,Quantity :").grid(row=2, column=0)
        self.new_item_entry = NewItemEntry(self)
        self.new_item_button = NewItemButton(self, function=self.write_new_item)
        self.new_item_error = Label(self, fg="red")
        self.new_item_error.grid(row=3, column=1)

        Label(self, text="Change item:\n (Leave empty if you don't want to change Cat::10)").grid(row=4, column=0)
        self.change_item_entry = ChangeItemEntry(self)
        self.change_item_button = ChangeItemButton(self, function=self.change_item)
        self.change_item_error = Label(self, fg="red")
        self.change_item_error.grid(row=5, column=1)

    def delete_item(self):
        name = self.delete_entry.get()
        names = functions.get_names()

        if name not in names:
            self.delete_error.config(text="This item is not in the inventory!")
        else:
            self.delete_error.config(text="")
            functions.delete_item(name)
            self.text_area()

    def write_new_item(self):
        names = functions.get_names()
        new_item = self.new_item_entry.get()
        new_item = new_item.split(',')

        try:
            name = new_item[0]
            price = float(new_item[1])
            quantity = int(new_item[2])

            if name in names:
                self.new_item_error.config(text="The name of the new item is already in the inventory !")
            else:
                functions.write_data(name, price, quantity)
                self.new_item_error.config(text="")
                self.text_area()
        except (ValueError, IndexError):
            self.new_item_error.config(text="The input is invalid !")

    def change_item(self):
        item = self.change_item_entry.get().split(':')
        names = functions.get_names()

        try:
            name = item[0]

            if name not in names:
                raise NameError

            if len(item[1]) != 0 and len(item[2]) != 0:
                price = float(item[1])
                quantity = int(item[2])
                functions.change_item_both(name, price, quantity)
            elif len(item[1]) != 0:
                price = float(item[1])
                functions.change_item_price(name, price)
            elif len(item[2]) != 0:
                quantity = int(item[2])
                functions.change_item_quantity(name, quantity)
            else:
                raise ValueError
        except NameError:
            self.change_item_error.config(text="The name of the item doesn't exist !")
        except (ValueError, IndexError):
            self.change_item_error.config(text="The input is invalid !")
        else:
            self.change_item_error.config(text="")
            self.text_area()


class SearchEntry(Entry):
    default_text = "Enter the name"

    def __init__(self, mater=None, **kwargs) -> None:
        super().__init__(mater, kwargs)
        self.pack()

        self.insert(0, self.default_text)
        self.bind("<FocusIn>", self.on_focus)
        self.bind("<FocusOut>", self.on_focus_out)
        self.configure(fg="grey")

    def on_focus(self, event):
        if self.get() == self.default_text:
            self.delete(0, END)
            self.configure(fg="black")

    def on_focus_out(self, event):
        if not self.get():
            self.insert(0, self.default_text)
            self.configure(fg='grey')


class SearchArea(Frame):
    labels = []

    def __init__(self, mater=None, **kwargs):
        super().__init__(mater, kwargs)
        self.pack(side=LEFT, fill=BOTH, expand=True)

        Label(self, text="Search here :").pack()

        self.search_bar = SearchEntry(self)
        self.search_bar.bind("<Key>", self.search)

    def search(self, event):

        for label in self.labels:
            label.destroy()
        self.labels.clear()

        search_term = self.search_bar.get()

        if search_term == SearchEntry.default_text:
            return

        results = functions.search_for_item(search_term)

        for result in results:
            self.labels.append(Label(self, text=result))
            self.labels[-1].pack()


class TextArea(LabelFrame):

    def __init__(self, master=None, **kwargs) -> None:
        super().__init__(master, **kwargs)
        self.pack(side=LEFT, fill=Y)

        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(0, weight=1)

        self.scrollbar = Scrollbar(self, orient="vertical")
        self.text = Text(self, yscrollcommand=self.scrollbar.set, state="disabled")
        self.set_text()

        self.text.grid(row=0, column=0, sticky="nsew")
        self.text.config(font=("Arial", 12))

        self.scrollbar.grid(row=0, column=1, sticky="nsew")
        self.scrollbar.config(command=self.text.yview)

    def set_text(self):
        self.text.config(state="normal")
        self.text.delete("1.0", END)
        self.text.insert(END, functions.get_data())
        self.text.config(state="disabled")

    def __call__(self, *args, **kwarg):
        self.set_text()


class MyWindow(Tk):
    def __init__(self) -> None:
        super().__init__()

        self.geometry("400x500")
        self.title("Inventory Management System")

        self.text_area = TextArea(self, width=self.winfo_width() // 3, height=self.winfo_height())
        self.search_area = SearchArea(self)
        self.function_area = FunctionArea(self, text_area=self.text_area)

        self.mainloop()



