import sys
import json
from functools import partial
import mysql.connector
from PyQt6.QtGui import QCloseEvent
from PyQt6.QtWidgets import QApplication, QWidget, QLabel, QPushButton, QGridLayout, QLineEdit, QMessageBox


class Data:
    items: list[dict] = []
    username: str
    password: str

    def __init__(self) -> None:
        self.get_login_info()
        self.retrieve_data()

    def get_login_info(self) -> None:
        with open("login_info.json", "r") as file:
            login_info = json.load(file)
            self.username = login_info["username"]
            self.password = login_info["password"]

    def retrieve_data(self) -> None:
        connection = None
        cursor = None
        try:
            connection = mysql.connector.connect(host="localhost", user=self.username, password=self.password,
                                                 database="InventoryManagementSystem")
            cursor = connection.cursor()
            query = "SELECT * FROM PythonData"

            cursor.execute(query)
            data = cursor.fetchall()

            for row in data:
                item = {
                    "id": row[0],
                    "name": row[1],
                    "price": float(row[2]),
                    "quantity": row[3]
                }
                self.items.append(item)

        except mysql.connector.Error as err:
            print("Error while connecting to the database for getting data ", err)
            sys.exit(1)
        finally:
            if connection is not None:
                connection.close()
            if cursor is not None:
                cursor.close()

    def save_data(self) -> None:
        connection = None
        cursor = None

        try:
            connection = mysql.connector.connect(host="localhost", user=self.username, password=self.password,
                                                 database="InventoryManagementSystem")
            cursor = connection.cursor()
            query = "DELETE FROM PythonData"
            cursor.execute(query)

            for item in self.items:
                query = "INSERT INTO PythonData(name, price, quantity) VALUES (%s, %s, %s)"
                cursor.execute(query, (item["name"], item["price"], item["quantity"]))

        except mysql.connector.Error as err:
            print("Error while connecting to the database for saving data ", err)
            sys.exit(1)
        finally:
            if connection is not None:
                connection.commit()
                connection.close()
            if cursor is not None:
                cursor.close()

    def delete_item(self, _id: int) -> None:
        for item in self.items:
            if item["id"] == _id:
                self.items.remove(item)

    def change_item_values(self, _id: int, price: float, quantity: int) -> None:
        for item in self.items:
            if item["id"] == _id:
                item["price"] = price
                item["quantity"] = quantity

    def add_item(self, name: str, price: float, quantity: int) -> None:
        item = {
            "id": self.get_new_id(),
            "name": name,
            "price": price,
            "quantity": quantity
        }
        self.items.append(item)

    def get_new_id(self) -> int:
        try:
            return self.items[-1]["id"] + 1
        except IndexError:
            return 0

    def __iter__(self) -> dict:
        for item in self.items:
            yield item


class NewItemWindow(QWidget):
    window: "Window"
    data: Data
    nameLineEdit: QLineEdit
    priceLineEdit: QLineEdit
    quantityLineEdit: QLineEdit

    def __init__(self, window: "Window", data: "Data") -> None:
        super().__init__()

        # setting attributes to the widgets
        self.setWindowTitle("New Item")
        self.setGeometry(200, 200, 300, 300)

        # passing the arguments
        self.window = window
        self.data = data

        # creating the widgets
        layout = QGridLayout()
        self.nameLineEdit = QLineEdit()
        self.priceLineEdit = QLineEdit()
        self.quantityLineEdit = QLineEdit()
        add_button = QPushButton("Add item")

        # connecting the button
        add_button.clicked.connect(self.on_add_button_clicked)

        # adding the widgets
        layout.addWidget(QLabel("Name"), 0, 0)
        layout.addWidget(self.nameLineEdit, 0, 1)
        layout.addWidget(QLabel("Price"), 1, 0)
        layout.addWidget(self.priceLineEdit, 1, 1)
        layout.addWidget(QLabel("Quantity"), 2, 0)
        layout.addWidget(self.quantityLineEdit, 2, 1)
        layout.addWidget(add_button, 3, 0)

        self.setLayout(layout)

    def on_add_button_clicked(self) -> None:
        name = self.nameLineEdit.text()
        if len(name) == 0:
            QMessageBox.warning(self, "Error", "The value in name in invalid")
            return
        try:
            price = float(self.priceLineEdit.text())
        except ValueError:
            self.priceLineEdit.setText("")
            QMessageBox.warning(self, "Error", "The value in price in invalid")
            return

        try:
            quantity = int(self.quantityLineEdit.text())
        except ValueError:
            self.quantityLineEdit.setText("")
            QMessageBox.warning(self, "Error", "The value in quantity in invalid")
            return

        self.data.add_item(name, price, quantity)
        self.window.on_search()
        self.close()


class ItemInfo(QWidget):
    window: "Window"
    data: Data
    _id: int
    priceLineEdit: QLineEdit
    quantityLineEdit: QLineEdit

    def __init__(self, window: "Window", data: Data, item: dict) -> None:
        super().__init__()

        # setting attributes to the window
        self.setWindowTitle(item["name"])
        self.setGeometry(200, 200, 300, 300)

        # passing the arguments
        self.window = window
        self.data = data
        self.item_id = item["id"]

        # creating the widgets
        layout = QGridLayout()
        self.priceLineEdit = QLineEdit()
        self.quantityLineEdit = QLineEdit()
        save_button = QPushButton("Save")
        delete_button = QPushButton("Delete")

        # setting attributes and connecting the widgets
        self.priceLineEdit.setText(str(item["price"]))
        self.quantityLineEdit.setText(str(item["quantity"]))
        save_button.clicked.connect(self.on_save_clicked)
        delete_button.clicked.connect(self.on_delete_clicked)

        # adding the widgets
        layout.addWidget(QLabel("Price"), 0, 0)
        layout.addWidget(self.priceLineEdit, 0, 1)
        layout.addWidget(QLabel("Quantity"), 1, 0)
        layout.addWidget(self.quantityLineEdit, 1, 1)
        layout.addWidget(save_button, 3, 0)
        layout.addWidget(delete_button, 4, 0)

        self.setLayout(layout)

    def on_save_clicked(self) -> None:
        try:
            price = float(self.priceLineEdit.text())
        except ValueError:
            self.priceLineEdit.setText("")
            QMessageBox.warning(self, "Error", "The value in price in invalid")
            return

        try:
            quantity = int(self.quantityLineEdit.text())
        except ValueError:
            self.quantityLineEdit.setText("")
            QMessageBox.warning(self, "Error", "The value in quantity in invalid")
            return

        self.data.change_item_values(self.item_id, price, quantity)
        self.window.on_search()
        self.close()

    def on_delete_clicked(self) -> None:
        self.data.delete_item(self.item_id)
        self.window.on_search()
        self.close()


class Window(QWidget):
    data = Data()
    layout: QGridLayout
    searchBar: QLineEdit
    results: list[QPushButton] = []
    itemInfoWindow: ItemInfo
    newItemWindow: NewItemWindow

    def __init__(self) -> None:
        super().__init__()

        # setting attributes to the window
        self.setWindowTitle("Inventory Management System")
        self.setGeometry(100, 100, 500, 500)

        # creating the widgets
        self.layout = QGridLayout()
        add_button = QPushButton("Add item")
        self.searchBar = QLineEdit()

        # adding the widgets
        self.layout.addWidget(add_button, 0, 0)
        self.layout.addWidget(self.searchBar, 1, 0)

        # connecting the widgets
        add_button.clicked.connect(self.on_add_clicked)
        self.searchBar.returnPressed.connect(self.on_search)

        self.setLayout(self.layout)

    def on_add_clicked(self) -> None:
        self.newItemWindow = NewItemWindow(self, self.data)
        self.newItemWindow.show()

    def on_search(self) -> None:
        for result in self.results:
            self.layout.removeWidget(result)
        self.results.clear()

        text = self.searchBar.text()
        row = 2

        for item in self.data:
            if item["name"].startswith(text):
                result = QPushButton(item["name"])
                self.results.append(result)

                result.clicked.connect(partial(self.on_result_clicked, item))

                self.layout.addWidget(result, row, 0)
                row += 1

    def on_result_clicked(self, item: dict) -> None:
        self.itemInfoWindow = ItemInfo(self, self.data, item)
        self.itemInfoWindow.show()

    def closeEvent(self, event: QCloseEvent) -> None:
        self.data.save_data()
        event.accept()
        sys.exit(0)


if __name__ == "__main__":
    app = QApplication(sys.argv)
    wnd = Window()
    wnd.show()
    app.exec()
