#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <float.h>

#define MAX_PRODUCTS 1000
#define MAX_NAME_LENGTH 100
#define FILE_NAME "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/c/data.txt"

struct Item {
    int quantity;
    double price;
    char name[MAX_NAME_LENGTH];
};
int count = 0;
struct Item inventory[MAX_PRODUCTS];

void load_products() {
    FILE *file = fopen(FILE_NAME, "r");
    if (file == NULL) {
        perror("Error opening file for reading");
        exit(EXIT_FAILURE);
    }

    while (fscanf(file, "%99[^,],%lf,%d\n", inventory[count].name, &inventory[count].price,
                  &inventory[count].quantity) == 3) {
        count++;
    }

    fclose(file);
}

void write_product(int quantity, double price, const char *name) {
    FILE *file = fopen(FILE_NAME, "a");
    if (file == NULL) {
        perror("Error opening file for writing");
        exit(EXIT_FAILURE);
    }

    fprintf(file, "%s,%.2lf,%d\n", name, price, quantity);
    fclose(file);
}

void delete_file() {
    FILE *file = fopen(FILE_NAME, "w");
    fclose(file);
}

void show_inventory() {
    for (int i = 0; i < count; i++) {
        printf("Name: %s \n", inventory[i].name);
        printf("Price: %lf \n", inventory[i].price);
        printf("Quantity: %d \n\n\n", inventory[i].quantity);
    }
}

double calculate_total_price() {
    double price = 0;

    for (int i = 0; i < count; i++) {
        price += inventory[i].price * inventory[i].quantity;
    }

    return price;
}

double get_most_expensive_item() {
    double result = 0;

    for (int i = 0; i < count; i++) {
        if (result < inventory[i].price) {
            result = inventory[i].price;
        }
    }

    return result;
}

double get_least_expensive_price() {
    double result = DBL_MAX;

    for (int i = 0; i < count; i++) {
        if (result > inventory[i].price) {
            result = inventory[i].price;
        }
    }

    return result;
}

char *get_most_expensive_item_name() {
    char *result;
    double price = 0;

    for (int i = 0; i < count; i++) {
        if (price < inventory[i].price) {
            price = inventory[i].price;
            result = inventory[i].name;
        }
    }

    return result;
}

char *get_least_expensive_item_name() {
    char *result;
    double price = DBL_MAX;

    for (int i = 0; i < count; i++) {
        if (price > inventory[i].price) {
            price = inventory[i].price;
            result = inventory[i].name;
        }
    }

    return result;
}

int main() {
    double price;
    char name[MAX_NAME_LENGTH];
    int quantity;
    int choice = 0;

    do {
        load_products();

        printf("Enter your choice \n");
        printf("1 - Add new item \n");
        printf("2 - Show all items \n");
        printf("3 - Get the total value \n");
        printf("4 - Get the most expensive item \n");
        printf("5 - Get the least expensive item \n");
        printf("6 - Delete all items \n");
        printf("7 - Exit the program \n");
        scanf("%d", &choice);
        getchar();

        switch (choice) {
            case 1: {
                printf("Enter the name:");
                scanf("%[^\n]", name);
                printf("Enter the price:");
                scanf("%lf", &price);
                printf("Enter the quantity:");
                scanf("%d", &quantity);
                write_product(quantity, price, name);
                break;
            }
            case 2: {
                show_inventory();
                break;
            }
            case 3: {
                printf("The total value = %lf\n", calculate_total_price());
                break;
            }
            case 4: {
                printf("%s is the most expensive item with price = %lf\n", get_most_expensive_item_name(),
                       get_most_expensive_item());
                break;
            }
            case 5: {
                printf("%s is the least expensive item with price = %lf\n", get_least_expensive_item_name(),
                       get_least_expensive_price());
                break;
            }
            case 6: {
                delete_file();
                break;
            }
            case 7: {
                printf("Exiting the program !\n");
                break;
            }
            default: {
                printf("You entered invalid option !\n");
                break;
            }
        }
    } while (choice != 7);
    return 0;
}