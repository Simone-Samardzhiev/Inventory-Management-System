import json
import sys

from PyQt6.QtGui import QCloseEvent
from PyQt6.QtWidgets import QApplication, QWidget, QLabel, QPushButton, QGridLayout, QLineEdit


class Data:
    path: str = '/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/python/inventory.json'
    data: list[dict]

    def __init__(self) -> None:
        self.read_data()

    def read_data(self) -> None:
        with open(self.path, "r") as file:
            self.data = json.load(file)

    def write_data(self) -> None:
        with open(self.path, "w") as file:
            json.dump(self.data, file, indent=4)

    def check_if_name_exists(self, name: str) -> bool:
        for item in self.data:
            if item["name"] == name:
                return True

        return False

    def change_item_attributes(self, name: str, price: float, quantity: int) -> None:
        for item in self.data:
            if item["name"] == name:
                item["price"] = price
                item["quantity"] = quantity

    def delete_item(self, name: str) -> None:
        self.data = [item for item in self.data if item['name'] != name]

    def add_item(self, name: str, price: float, quantity: int) -> None:
        item = {"name": name, "price": price, "quantity": quantity}
        self.data.append(item)

    def iter_over_items(self) -> dict:
        for item in self.data:
            yield item


class AddItem(QWidget):
    data: Data
    layout: QGridLayout
    nameEntry: QLineEdit
    priceEntry: QLineEdit
    quantityEntry: QLineEdit
    button: QPushButton

    def __init__(self, data: Data) -> None:
        super().__init__()
        self.setWindowTitle('Add Item')
        self.setGeometry(200, 200, 400, 400)

        self.data = data

        self.layout = QGridLayout()

        self.layout.addWidget(QLabel('Enter the name : '), 0, 0)

        self.nameEntry = QLineEdit()
        self.layout.addWidget(self.nameEntry, 0, 1)

        self.layout.addWidget(QLabel("Enter the price : "), 1, 0)

        self.priceEntry = QLineEdit()
        self.layout.addWidget(self.priceEntry, 1, 1)

        self.layout.addWidget(QLabel("Enter the quantity : "), 2, 0)

        self.quantityEntry = QLineEdit()
        self.layout.addWidget(self.quantityEntry, 2, 1)

        self.button = QPushButton('Add')
        self.button.clicked.connect(self.on_add_clicked)
        self.layout.addWidget(self.button, 3, 0)

        self.setLayout(self.layout)
        self.show()

    def on_add_clicked(self) -> None:
        try:
            name = self.nameEntry.text()
            price = float(self.priceEntry.text())
            quantity = int(self.quantityEntry.text())

            if self.data.check_if_name_exists(name):
                self.nameEntry.setPlaceholderText("The item exists")
                self.nameEntry.setText("")
            else:
                self.data.add_item(name, price, quantity)
                self.close()

        except ValueError:
            self.priceEntry.setPlaceholderText("The values are not numbers")
            self.priceEntry.setText("")
            self.quantityEntry.setPlaceholderText("The values are not numbers")
            self.nameEntry.setText("")


class ItemInfo(QWidget):
    layout: QGridLayout
    quantityEntry: QLineEdit
    priceEntry: QLineEdit
    buttonForSaving: QPushButton
    buttonForDeleting: QPushButton
    data: Data
    item: dict

    def __init__(self, item: dict, data: Data) -> None:
        super().__init__()

        self.setWindowTitle(item['name'])
        self.setGeometry(100, 100, 400, 400)

        self.data = data
        self.item = item

        self.layout = QGridLayout()

        self.layout.addWidget(QLabel("Price :"), 0, 0)

        self.priceEntry = QLineEdit()
        self.priceEntry.setText(str(self.item["price"]))
        self.layout.addWidget(self.priceEntry, 0, 1)

        self.layout.addWidget(QLabel('Quantity :'), 1, 0)

        self.quantityEntry = QLineEdit()
        self.quantityEntry.setText(str(self.item["quantity"]))
        self.layout.addWidget(self.quantityEntry, 1, 1)

        self.buttonForSaving = QPushButton('Save')
        self.buttonForSaving.clicked.connect(self.on_save_pressed)
        self.layout.addWidget(self.buttonForSaving, 2, 0)

        self.buttonForDeleting = QPushButton('Delete')
        self.buttonForDeleting.clicked.connect(self.on_delete_pressed)
        self.layout.addWidget(self.buttonForDeleting, 3, 0)

        self.setLayout(self.layout)
        self.show()

    def on_save_pressed(self):
        try:
            price = float(self.priceEntry.text())
            quantity = int(self.quantityEntry.text())
            name = self.item['name']
            self.data.change_item_attributes(name, price, quantity)
            self.close()

        except ValueError:
            self.priceEntry.setPlaceholderText("The values are not numbers")
            self.priceEntry.setText("")
            self.quantityEntry.setPlaceholderText("The values are not numbers")
            self.quantityEntry.setText("")

    def on_delete_pressed(self):
        self.data.delete_item(self.item['name'])
        self.close()


class Window(QWidget):
    mainLayout: QGridLayout
    buttonForAddingItem: QPushButton
    searchEntry: QLineEdit
    results: list[QPushButton] = []
    data: Data = Data()
    windows: list = []

    def __init__(self) -> None:
        super().__init__()

        self.setWindowTitle('Inventory Management System')
        self.setGeometry(100, 100, 600, 600)

        self.mainLayout = QGridLayout()

        self.buttonForAddingItem = QPushButton("Add Item")
        self.buttonForAddingItem.clicked.connect(self.on_add_pressed)
        self.mainLayout.addWidget(self.buttonForAddingItem, 0, 0)

        self.searchEntry = QLineEdit()
        self.searchEntry.textChanged.connect(self.on_search)
        self.mainLayout.addWidget(self.searchEntry, 1, 0)

        self.setLayout(self.mainLayout)

    def on_search(self, text: str) -> None:
        for result in self.results:
            self.mainLayout.removeWidget(result)

        self.results.clear()
        text = text.lower()

        row = 2
        for item in self.data.iter_over_items():
            if item["name"].startswith(text):
                button = QPushButton(item["name"])
                button.clicked.connect(lambda: self.on_result_pressed(item))
                self.mainLayout.addWidget(button, row, 0)
                row += 1
                self.results.append(button)

    def on_result_pressed(self, item: dict) -> None:
        self.windows.clear()
        self.windows.append(ItemInfo(item, self.data))

    def on_add_pressed(self) -> None:
        self.windows.clear()
        self.windows.append(AddItem(self.data))

    def closeEvent(self, event: QCloseEvent) -> None:
        self.windows.clear()
        self.data.write_data()
        event.accept()


if __name__ == '__main__':
    app = QApplication(sys.argv)
    wnd = Window()
    wnd.show()
    sys.exit(app.exec())
