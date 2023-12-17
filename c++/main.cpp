#include <iostream>
#include <vector>
#include <fstream>
#include <sstream>
#include <string>

#define FILE_NAME "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/c++/inventory.txt"

using namespace std;

class item {
private:
    string name;
    double price;
    int quantity;

public:
    item(string n, double p, int q) : name(n), price(p), quantity(q) {}

    void show() {
        cout << "Name: " << name << endl;
        cout << "Price: " << price << endl;
        cout << "Quantity: " << quantity << endl << endl;
    }

    double get_value() const {
        return price * quantity;
    }

    string get_name() const {
        return name;
    }

    double get_price() {
        return price;
    }

    int get_quantity() {
        return quantity;
    }
};

vector<item> inventory;

void load_inventory() {
    inventory.clear();
    ifstream file(FILE_NAME);
    string line;

    if (!file.is_open()) {
        cerr << "There was an error in opening the file !" << endl;
        terminate();
    }

    while (getline(file, line)) {
        stringstream ss(line);
        string name;
        string price_str;
        string quantity_str;

        if (getline(ss, name, ',') && getline(ss, price_str, ',') && getline(ss, quantity_str)) {
            double price = stod(price_str);
            int quantity = stoi(quantity_str);
            inventory.emplace_back(name, price, quantity);
        }
    }
    file.close();
}

void write_item(string name, double price, int quantity) {
    ofstream file(FILE_NAME, ios::app);

    if (!file.is_open()) {
        cerr << "There was an error is opening the file !" << endl;
        terminate();
    }

    file << name << ',' << price << ',' << quantity << endl;
}

void delete_item(const string &name) {
    ofstream file(FILE_NAME);
    int i;
    if (!file.is_open()) {
        cerr << "There was an error in opening the file!" << endl;
        terminate();
    }

    for (i = 0; i < inventory.size(); i++) {
        if (inventory[i].get_name() != name) {
            file << inventory[i].get_name() << ',' << inventory[i].get_price() << ',' << inventory[i].get_quantity()
                 << endl;
        }
    }

    file.close();
    inventory.erase(inventory.begin() + i);
}


double calculate_total_value() {
    double sum = 0;

    for (int i = 0; i < inventory.size(); i++) {
        sum += inventory[i].get_value();
    }

    return sum;
}


void show_items() {
    for (int i = 0; i < inventory.size(); i++) {
        inventory[i].show();
    }
}

int main() {
    int choice;
    string name;
    double price;
    int quantity;

    do {
        load_inventory();
        cout << "Enter you choice" << endl;
        cout << "1 - show inventory" << endl;
        cout << "2 - add new item" << endl;
        cout << "3 - show total value " << endl;
        cout << "4 - Exit the program" << endl;
        cin >> choice;
        switch (choice) {
            case 1: {
                show_items();
                break;
            }
            case 2: {
                cout << "Enter the name:";
                cin.ignore();
                getline(cin, name);
                cout << "Enter the price:";
                cin >> price;
                cout << "Enter the quantity:";
                cin >> quantity;

                write_item(name, price, quantity);
            }
            case 3: {
                cout << calculate_total_value() << endl;
                break;
            }
            case 4: {
                cout << "Exiting the program !" << endl;
                break;
            }
            default: {
                cout << "You entered invalid option !" << endl;
            }
        }
    } while (choice != 4);
    return 0;
}