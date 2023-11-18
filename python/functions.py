import json
import os

PATH = "/home/simone/Desktop/My projects/Inventory Management System/python/inventory.json"
inventory = []


def read_data() -> list[dict]:
    global inventory
    inventory = []
    if os.path.getsize(PATH) > 0:
        with open(PATH, "r") as file:
            inventory = json.load(file)

    return inventory


def write_data(name: str, price: float, quantity: int) -> None:
    global inventory
    data = {
        "name": name,
        "price": price,
        "quantity": quantity
    }

    inventory.append(data)

    with open(PATH, "w") as file:
        json.dump(inventory, file, indent=4)


def get_data() -> str:
    global inventory
    read_data()
    result = ''

    for item in inventory:
        result += f"Name: {item['name']}\n"
        result += f"Price: {item['price']}\n"
        result += f"Quantity: {item['quantity']}\n\n"

    return result


def get_names() -> list[str]:
    global inventory
    result = []

    for item in inventory:
        result.append(item['name'])

    return result


def clean_file() -> None:
    file = open(PATH, "w")
    file.close()


def delete_item(name: str) -> None:
    global inventory
    clean_file()

    inventory = [item for item in inventory if item['name'] != name]

    with open(PATH, "w") as file:
        json.dump(inventory, file, indent=4)

    read_data()


def search_for_item(symbols: str) -> list[str]:
    result = []

    if symbols == "Enter the name" or symbols == '':
        return result

    for item in inventory:
        if item['name'].lower().startswith(symbols.lower()):
            result.append(f"Name: {item['name']}, Price: {item['price']}, Quantity: {item['quantity']}")

    return result


def change_item_price(name: str, price: float) -> None:
    global inventory

    for item in inventory:
        if item['name'] == name:
            item['price'] = price

    with open(PATH, "w") as file:
        json.dump(inventory, file, indent=4)


def change_item_quantity(name: str, quantity: int) -> None:
    global inventory

    for item in inventory:
        if item['name'] == name:
            item['quantity'] = quantity

    with open(PATH, "w") as file:
        json.dump(inventory, file, indent=4)


def change_item_both(name: str, price: float, quantity: int) -> None:
    global inventory

    for item in inventory:
        if item['name'] == name:
            item['price'], item['quantity'] = price, quantity

    with open(PATH, "w") as file:
        json.dump(inventory, file, indent=4)

