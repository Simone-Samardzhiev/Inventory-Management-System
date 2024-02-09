import sys
from Data import Data, Item
from functools import partial

from PyQt6.QtWidgets import (QMainWindow, QWidget, QGridLayout, QVBoxLayout, QMessageBox, QFrame, QLabel, QLineEdit,
                             QPushButton, QApplication, QScrollArea)
from PyQt6.QtGui import QCloseEvent


class AddItemWindow(QWidget):
    # Global layout used for the window.
    layout: QGridLayout
    # Object used to interact with data.
    data: Data
    # Variable that will hold the main window to change the UI when the data is changed.
    mainWindow: "MainWindow"
    # QLineEdits used to gat the information about the item.
    nameLineEdit: QLineEdit
    priceLineEdit: QLineEdit
    quantityLineEdit: QLineEdit

    def __init__(self, data: Data, window: "MainWindow") -> None:
        super().__init__()

        # Setting attributes to the window.
        self.setWindowTitle("Add Item Window")
        self.setGeometry(300, 300, 500, 500)

        # Passing the arguments.
        self.data = data
        self.mainWindow = window

        # Creating the UI.
        self.layout = QGridLayout()
        self.setLayout(self.layout)

        # Calling create_ui to create the UI.
        self.create_ui()

        # Showing the window
        self.show()

    # noinspection DuplicatedCode
    def create_ui(self):
        # Creating the widgets.
        self.nameLineEdit = QLineEdit()
        self.priceLineEdit = QLineEdit()
        self.quantityLineEdit = QLineEdit()
        add_button = QPushButton("Add item")

        # Connecting the widgets.
        add_button.clicked.connect(self.on_add_button_clicked)

        # Adding the widgets
        self.layout.addWidget(QLabel("Item Name:"), 0, 0)
        self.layout.addWidget(self.nameLineEdit, 0, 1)
        self.layout.addWidget(QLabel("Item Price:"), 1, 0)
        self.layout.addWidget(self.priceLineEdit, 1, 1)
        self.layout.addWidget(QLabel("Item Quantity:"), 2, 0)
        self.layout.addWidget(self.quantityLineEdit, 2, 1)
        self.layout.addWidget(add_button, 3, 0)

    # Method that will be called when the add button is clicked.
    def on_add_button_clicked(self) -> None:
        # noinspection DuplicatedCode
        name = self.nameLineEdit.text()

        if len(name) == 0:
            QMessageBox.warning(self, "Error", "The name can't be empty")
            return
        try:
            price = float(self.priceLineEdit.text())
            if price < 0:
                QMessageBox.warning(self, "Error", "The price can't be less than zero.")
                return
        except ValueError:
            QMessageBox.warning(self, "Error", "Please enter a valid price")
            return

        try:
            quantity = int(self.quantityLineEdit.text())
            if quantity < 0:
                QMessageBox.warning(self, "Error", "The quantity can't be less than zero.")
                return
        except ValueError:
            QMessageBox.warning(self, "Error", "Please enter a valid quantity")
            return

        self.data.add_item(name, price, quantity)

        # Updating the UI of the main window and closing the information window.
        self.mainWindow.on_search()
        self.close()


class ItemInfoWindow(QMainWindow):
    # QWidget that will display the UI.
    centralWidget: QWidget
    # Global layout used for the window.
    layout: QGridLayout
    # QLineEdits used for the information about the item.
    nameLineEdit: QLineEdit
    priceLineEdit: QLineEdit
    quantityLineEdit: QLineEdit
    # Variable that will hold the item that is shown
    item: Item
    # Variable that will hold the data used to interact with the server.
    data: Data
    # Variable that will hold the main window to update the UI if changes are made.
    mainWindow: "MainWindow"

    def __init__(self, item: Item, data: Data, window: "MainWindow") -> None:
        super().__init__()

        # Setting attributes to the window.
        self.setWindowTitle("Item information")
        self.setGeometry(300, 300, 400, 400)

        # Passing the arguments.
        self.item = item
        self.data = data
        self.mainWindow = window

        # Creating the UI.
        self.centralWidget = QWidget()
        self.setCentralWidget(self.centralWidget)
        self.layout = QGridLayout()
        self.centralWidget.setLayout(self.layout)

        # Calling create_ui to create the UI in the window
        self.create_ui()

        # Showing the window
        self.show()

    # Method that is used to create the UI.
    def create_ui(self) -> None:
        # Creating the widgets
        self.nameLineEdit = QLineEdit(self.item.name)
        self.priceLineEdit = QLineEdit(str(self.item.price))
        self.quantityLineEdit = QLineEdit(str(self.item.quantity))
        save_button = QPushButton("Save item")
        delete_button = QPushButton("Delete item")

        # Connecting the widgets.
        save_button.clicked.connect(self.on_save_button_clicked)
        delete_button.clicked.connect(self.on_delete_button_clicked)

        # Adding the widgets
        # noinspection DuplicatedCode
        self.layout.addWidget(QLabel("Item Name:"), 0, 0)
        self.layout.addWidget(self.nameLineEdit, 0, 1)
        self.layout.addWidget(QLabel("Item Price:"), 1, 0)
        self.layout.addWidget(self.priceLineEdit, 1, 1)
        self.layout.addWidget(QLabel("Item Quantity:"), 2, 0)
        self.layout.addWidget(self.quantityLineEdit, 2, 1)
        self.layout.addWidget(save_button, 3, 0)
        self.layout.addWidget(delete_button, 3, 1)

    # Method called when the save button is clicked. It will try to parse the values and tell the user if it fails.
    # If the values are correct, it will save the changes
    def on_save_button_clicked(self) -> None:
        # noinspection DuplicatedCode
        name = self.nameLineEdit.text()

        if len(name) == 0:
            QMessageBox.warning(self, "Error", "The name can't be empty")
            return

        try:
            price = float(self.priceLineEdit.text())
            if price < 0:
                QMessageBox.warning(self, "Error", "The price can't be less than zero.")
                return
        except ValueError:
            QMessageBox.warning(self, "Error", "Please enter a valid price")
            return

        try:
            quantity = int(self.quantityLineEdit.text())
            if quantity < 0:
                QMessageBox.warning(self, "Error", "The quantity can't be less than zero.")
                return
        except ValueError:
            QMessageBox.warning(self, "Error", "Please enter a valid quantity")
            return

        self.item.name = name
        self.item.price = price
        self.item.quantity = quantity

        # Updating the UI of the main window and closing the information window.
        self.mainWindow.on_search()
        self.close()

    def on_delete_button_clicked(self) -> None:
        self.data.delete_item(self.item.id)

        # Updating the UI of the main window and closing the information window.
        self.mainWindow.on_search()
        self.close()


class MainWindow(QMainWindow):
    # QWidget that will display the UI.
    centralWidget: QWidget
    # Global layout used for the window.
    layout: QGridLayout
    # QLineEdit used by the user to search for items.
    searchBar: QLineEdit
    # Layout used to display the results forms the search bar.
    results_layout: QVBoxLayout
    # Object used to interact with the data
    data: Data
    # Variable that holds the login window, so when the main window is closed, the login window will open again.
    loginWindow: "LoginWindow"
    # Variable that will hold the window that is used to show information about the item.
    itemInfoWindow: ItemInfoWindow
    # Variable that will hold the window that is used to add a new item.
    addItemWindow: AddItemWindow

    def __init__(self, user_id, login_window: "LoginWindow") -> None:
        super().__init__()

        # Setting attributes to the window.
        self.setWindowTitle("Inventory management system")
        self.setGeometry(300, 300, 500, 500)

        # Using the arguments
        self.data = Data(user_id)
        self.loginWindow = login_window

        # Creating the UI.
        self.centralWidget = QWidget()
        self.setCentralWidget(self.centralWidget)
        self.layout = QGridLayout()
        self.centralWidget.setLayout(self.layout)

        # Calling create_ui to create the UI in the window
        self.create_ui()

        # Calling create_menu to create the menu of the window.
        self.create_menu()

        # Showing the window
        self.show()

    # Method that is used to create the UI.
    def create_ui(self) -> None:
        # Creating the widgets.
        self.searchBar = QLineEdit()
        self.results_layout = QVBoxLayout()
        results_frame = QFrame()
        results_scroll_area = QScrollArea()

        # Setting attributes.
        results_frame.setLayout(self.results_layout)
        results_scroll_area.setWidget(results_frame)
        results_scroll_area.setWidgetResizable(True)

        # Connecting the widgets.
        self.searchBar.returnPressed.connect(self.on_search)

        # Adding the widgets.
        self.layout.addWidget(self.searchBar, 0, 0)
        self.layout.addWidget(results_scroll_area, 1, 0)

    # Method that will be activated when the user clicks enter the search bar. It will display the found results.
    def on_search(self) -> None:
        for i in reversed(range(self.results_layout.count())):
            item = self.results_layout.takeAt(i)

            if item.widget():
                item.widget().deleteLater()

        text = self.searchBar.text().strip().lower()

        for item in self.data:
            if text in item.name:
                result = QPushButton(item.name)
                result.clicked.connect(partial(self.on_result_clicked, item))
                self.results_layout.addWidget(result)

    def on_result_clicked(self, item: Item) -> None:
        self.itemInfoWindow = ItemInfoWindow(item, self.data, self)

    # Method that is used to create the menu.
    def create_menu(self):
        # Getting the menu bar
        menu_bar = self.menuBar()

        # Creating the item menu.
        item_menu = menu_bar.addMenu("Item")

        # Adding actions to the item menu.
        item_menu.addAction("Add item", self.on_add_item_clicked)

        # Adding an inventory menu.
        inventory_menu = menu_bar.addMenu("Inventory")

        # Adding actions to the inventory menu.
        inventory_menu.addAction("Get total value", self.on_get_total_value_clicked)
        inventory_menu.addAction("Get most expensive item", self.on_get_most_expensive_item_clicked)
        inventory_menu.addAction("Get item with most largest quantity", self.on_get_item_with_highest_quantity_clicked)

    # Method activated when add item is clicked.
    def on_add_item_clicked(self) -> None:
        self.addItemWindow = AddItemWindow(self.data, self)

    def on_get_total_value_clicked(self) -> None:
        value: int = 0
        for item in self.data:
            value += item.price * item.quantity

        QMessageBox.warning(self, "Total value", f"The total value is: {value}")

    def on_get_most_expensive_item_clicked(self) -> None:
        QMessageBox.information(self, f"Most expensive item",
                                f"The most expensive item: {self.data.get_most_expensive_item().name}")

    def on_get_item_with_highest_quantity_clicked(self) -> None:
        QMessageBox.information(self, "Item with highest quantity",
                                f"The item with highest quantity: {self.data.get_item_with_most_quantity().name}")

    def closeEvent(self, event: QCloseEvent) -> None:
        self.loginWindow.emailLineEdit.setText("")
        self.loginWindow.passwordLineEdit.setText("")
        self.data.save_data()
        self.loginWindow.show()
        event.accept()


class LoginWindow(QWidget):
    # QLineEdits used for email and passwords.
    emailLineEdit: QLineEdit
    passwordLineEdit: QLineEdit

    # Global layout for the window.
    layout: QGridLayout

    # Main window that will be displayed when the user logs in successfully
    mainWindow: MainWindow

    def __init__(self) -> None:
        super().__init__()

        # Setting attributes to the window.
        self.setWindowTitle("Inventory management system")
        self.setGeometry(300, 300, 400, 400)

        # Creating the UI
        self.layout = QGridLayout()
        self.setLayout(self.layout)
        self.create_ui()

    def create_ui(self) -> None:
        # Creating the widgets.
        self.emailLineEdit = QLineEdit()
        self.passwordLineEdit = QLineEdit()
        login_button = QPushButton("Login")
        register_button = QPushButton("Register")

        # Setting attributes to the widgets
        self.passwordLineEdit.setEchoMode(QLineEdit.EchoMode.Password)

        # Connecting the widgets
        login_button.clicked.connect(self.on_login_button_clicked)
        register_button.clicked.connect(self.on_register_button_clicked)

        # Adding the widgets.
        self.layout.addWidget(QLabel("Email:"), 0, 0)
        self.layout.addWidget(self.emailLineEdit, 0, 1)
        self.layout.addWidget(QLabel("Password:"), 1, 0)
        self.layout.addWidget(self.passwordLineEdit, 1, 1)
        self.layout.addWidget(login_button, 2, 0)
        self.layout.addWidget(register_button, 2, 1)

    def on_login_button_clicked(self) -> None:
        email = self.emailLineEdit.text()
        password = self.passwordLineEdit.text()

        user_id = Data.login(email, password)

        if user_id is not None:
            self.mainWindow = MainWindow(user_id, self)
            self.hide()
        else:
            QMessageBox.warning(self, "Error", "The email or password is incorrect")

    def on_register_button_clicked(self) -> None:
        email = self.emailLineEdit.text()
        password = self.passwordLineEdit.text()

        if len(password) == 0 or len(email) == 0:
            QMessageBox.warning(self, "Error", "The email or the password can't be empty")
            return

        if Data.register(email, password):
            QMessageBox.information(self, "Registration Successful", "Your account has been created")
        else:
            QMessageBox.warning(self, "Registration Failed", "The email is already registered")


def main() -> None:
    app = QApplication(sys.argv)
    window = LoginWindow()
    window.show()
    app.exec()


if __name__ == "__main__":
    main()
