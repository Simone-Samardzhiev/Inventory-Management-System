import sys

import mysql.connector


# Class that represent an item.
class Item:
    id: int  # Variable for id
    name: str  # Variable for name
    price: float  # Variable for price
    quantity: int  # Variable for quantity

    # Constructor for the object.
    def __init__(self, _id: int, name: str, price: float, quantity: int) -> None:
        self.id = _id
        self.name = name
        self.price = price
        self.quantity = quantity


# Class that will manage all the data.
class Data:
    # Method that will log the user. If the user is found, it will return the user id otherwise it will return none
    @staticmethod
    def login(email: str, password: str) -> None | int:
        try:
            # Connecting to the database.
            with mysql.connector.connect(host='localhost', user='root', password='Simone2006',
                                         database='InventoryManagementSystem') as connection:
                cursor = connection.cursor()
                query = "SELECT id FROM Users WHERE email = %s AND password = %s;"
                cursor.execute(query, (email, password))
                row = cursor.fetchone()

                # Checking if the user is found.
                if row is not None:
                    return row[0]
                else:
                    return None
        # Catching the error and printing what's wrong.
        except mysql.connector.Error as err:
            print("There was an error in the login method in the data class ", err)
            sys.exit(1)

    # Method that will register the user. If the email is already in the database, it will return false and won't
    # register the user otherwise it will add the né account in the server.
    @staticmethod
    def register(email: str, password: str) -> bool:
        try:
            # Connecting to the database.
            with mysql.connector.connect(host='localhost', user='root', password='Simone2006',
                                         database='InventoryManagementSystem') as connection:
                cursor = connection.cursor()
                query = "SELECT COUNT(*) FROM Users WHERE email = %s and password = %s;"
                cursor.execute(query, (email, password))
                row = cursor.fetchone()
                # Checking if the email is already in the database.
                if row[0] == 0:
                    query = "INSERT INTO Users(email, password) VALUES (%s,%s);"
                    cursor.execute(query, (email, password))
                    connection.commit()
                    return True
                else:
                    return False
        # Catching the error and printing what's wrong.
        except mysql.connector.Error as err:
            print("There was an error in the register method in class data ", err)
            sys.exit(1)

    # Constructor for creating the data object
    def __init__(self, user_id: int) -> None:
        self.user_id = user_id  # Variable that keeps the user id
        self.items = []  # For that will contain the user items
        self.retrieve_data()  # Calling the method to retrieve data

    # Method that is user to retrieve all the items that are connected to the user.
    def retrieve_data(self) -> None:
        try:
            # Connecting to the database.
            with mysql.connector.connect(host='localhost', user='root', password='Simone2006',
                                         database='InventoryManagementSystem') as connection:
                cursor = connection.cursor()
                query = "SELECT id, name, price,quantity FROM Items WHERE userId = %s;"
                cursor.execute(query, (self.user_id,))

                for row in cursor.fetchall():
                    self.items.append(Item(row[0], row[1], row[2], row[3]))
        # Catching the error and printing what's wrong.
        except mysql.connector.Error as err:
            print("There was an error in the retrieve data method in class data ", err)
            sys.exit(1)

    # Method that will save any changes made to the items.
    def save_data(self) -> None:
        try:
            # Connecting to the database.
            with mysql.connector.connect(host='localhost', user='root', password='Simone2006',
                                         database='InventoryManagementSystem') as connection:
                cursor = connection.cursor()
                query = "UPDATE Items SET name = %s , price = %s,quantity = %s WHERE id = %s;"

                for item in self.items:
                    cursor.execute(query, (item.name, item.price, item.quantity, item.id))
                connection.commit()
        # Catching the error and printing what's wrong.
        except mysql.connector.Error as err:
            print("There was an error in the save data method in class data ", err)
            sys.exit(1)

    # Method that will add a new item.
    def add_item(self, name: str, price: float, quantity: int) -> None:
        try:
            # Connecting to the database.
            with mysql.connector.connect(host='localhost', user='root', password='Simone2006',
                                         database='InventoryManagementSystem') as connection:
                cursor = connection.cursor()
                query = "INSERT INTO Items (name, price, quantity, userId) VALUES (%s, %s, %s, %s);"
                cursor.execute(query, (name, price, quantity, self.user_id))
                connection.commit()

                self.items.append(Item(cursor.lastrowid, name, price, quantity))
        # Catching the error and printing what's wrong.
        except mysql.connector.Error as err:
            print("There was an error in the add add item method in class data ", err)
            sys.exit(1)

    # Method that will delete an item.
    def delete_item(self, item_id: int) -> None:
        try:
            # Connecting to the database.
            with mysql.connector.connect(host='localhost', user='root', password='Simone2006',
                                         database='InventoryManagementSystem') as connection:
                cursor = connection.cursor()
                query = "DELETE FROM Items WHERE id = %s;"
                cursor.execute(query, (item_id,))
                connection.commit()

                for item in self.items:
                    if item.id == item_id:
                        self.items.remove(item)
                        break
        # Catching the error and printing what's wrong.
        except mysql.connector.Error as err:
            print("There was an error in the delete item method in class data ", err)
            sys.exit(1)

    # Method that will return the most expensive item.
    def get_most_expensive_item(self) -> Item:
        price: float = 0
        result: Item | None = None
        for item in self.items:
            if item.price >= price:
                price = item.price
                result = item
        return result

    # Method that will return the item with most quantity.
    def get_item_with_most_quantity(self) -> Item:
        quantity: int = 0
        result: Item | None = None
        for item in self.items:
            if item.quantity >= quantity:
                quantity = item.quantity
                result = item
        return result

    # Overridden method user to iterate over the items.
    def __iter__(self) -> Item:
        for item in self.items:
            yield item
