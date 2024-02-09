#include <iostream>
#include <vector>
#include <mysqlx/xdevapi.h>

#include <QApplication>
#include <QMainWindow>
#include <QMenuBar>
#include <QMenu>
#include <QAction>
#include <QWidget>
#include <QMessageBox>
#include <QGridLayout>
#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QScrollArea>
#include <QFrame>

using std::string;
using std::cerr;
using std::endl;

// Class representing an item.
class Item {
private:
    int id;
    string name;
    double price;
    int quantity;

public:
    Item(int id, string name, double price, int quantity) : id(id), name(std::move(name)), price(price),
                                                            quantity(quantity) {}

    // Getters and setters.
    [[nodiscard]] string getName() const {
        return name;
    }

    void setName(const string &_name) {
        Item::name = _name;
    }

    [[nodiscard]] double getPrice() const {
        return price;
    }

    void setPrice(double _price) {
        Item::price = _price;
    }

    [[nodiscard]] int getQuantity() const {
        return quantity;
    }

    void setQuantity(int _quantity) {
        Item::quantity = _quantity;
    }

    [[nodiscard]] int getId() const {
        return id;
    }
};

// Class used to interact with the mysql server.
class Data {
public:
    // Method that is used to log in the person if their. It returns the user id if the user is found else it returns -1.
    static int Login(const string &email, const string &password) {
        int id = -1;

        try {
            mysqlx::Session session("mysqlx://root:Simone2006@localhost:33060");
            mysqlx::SqlStatement statement = session.sql("USE InventoryManagementSystem");
            statement.execute();
            statement = session.sql("SELECT id FROM Users WHERE email = ? and password = ?;").bind(email, password);
            mysqlx::SqlResult result = statement.execute();

            if (result.count() != 0) {
                mysqlx::Row row = result.fetchOne();
                id = row[0].get<int>();
            }

            session.close();
        }
        catch (const mysqlx::Error &error) {
            cerr << "There was an error in login method in class data: " << error << endl;
            exit(1);
        }

        return id;
    }

    // Method that is used to register a person. It will return true of the person is registered and false of the email is already in use.
    static bool Register(const string &email, const string &password) {
        try {
            mysqlx::Session session("mysqlx://root:Simone2006@localhost:33060");
            mysqlx::SqlStatement statement = session.sql("USE InventoryManagementSystem");
            statement.execute();
            statement = session.sql("SELECT COUNT(id) FROM Users where email = ?;").bind(email);
            mysqlx::SqlResult result = statement.execute();

            if (result.count() != 0) {
                mysqlx::Row row = result.fetchOne();

                if (row[0].get<int>() == 0) {
                    statement = session.sql("INSERT INTO Users(email, password) VALUES (?,?);").bind(email, password);
                    statement.execute();
                    return true;
                }
            }
            session.close();
        }
        catch (const mysqlx::Error &error) {
            cerr << "There was an error in register method in class data: " << error << endl;
            exit(1);
        }

        return false;
    }

private:
    std::vector<Item> items;
    int userId;

public:
    // Constructor that will create the data object using the id of the user.
    explicit Data(int userId) : userId(userId) {
        retrieveData();
    }

private:
    // Method used to retrieve data linked with the user.
    void retrieveData() {
        try {
            mysqlx::Session session("mysqlx://root:Simone2006@localhost:33060");
            mysqlx::SqlStatement statement = session.sql("USE InventoryManagementSystem");
            statement.execute();
            statement = session.sql("SELECT id, name, price, quantity FROM Items WHERE userId = ?;").bind(userId);
            mysqlx::SqlResult result = statement.execute();

            for (const mysqlx::Row &row: result) {
                int id = row[0].get<int>();
                string name = row[1].get<string>();
                double price = row[2].get<double>();
                int quantity = row[3].get<int>();
                items.emplace_back(id, name, price, quantity);
            }

            session.close();
        }
        catch (const mysqlx::Error &error) {
            cerr << "There was an error in retrieve data method in class data: " << error << endl;
            exit(1);
        }
    }

public:
    // Method used to save the data back to the mysql server.
    void saveData() {
        try {
            mysqlx::Session session("mysqlx://root:Simone2006@localhost:33060");
            mysqlx::SqlStatement statement = session.sql("USE InventoryManagementSystem");
            statement.execute();

            for (const Item &item: items) {
                statement = session.sql("UPDATE Items SET name = ?, price = ?, quantity = ? WHERE id = ?;").bind(
                        item.getName(), item.getPrice(), item.getQuantity(), item.getId());
                statement.execute();
            }

            session.close();
        }
        catch (const mysqlx::Error &error) {
            cerr << "There was and error in save data method in class data: " << error << endl;
            exit(1);
        }
    }

    // Method that is used to add a new item to the server.
    void addItem(const string &name, double price, int quantity) {
        try {
            mysqlx::Session session("mysqlx://root:Simone2006@localhost:33060");
            mysqlx::SqlStatement statement = session.sql("USE InventoryManagementSystem");
            statement.execute();
            statement = session.sql("INSERT INTO Items(name, price, quantity, userId) VALUES (?,?,?,?);").bind(
                    name, price, quantity, userId);
            statement.execute();
            statement = session.sql("SELECT LAST_INSERT_ID()");
            mysqlx::SqlResult result = statement.execute();

            if (result.count() != 0) {
                mysqlx::Row row = result.fetchOne();
                items.emplace_back(row[0].get<int>(), name, price, quantity);
            }

            session.close();
        }
        catch (const mysqlx::Error &error) {
            cerr << "There was an error in add item method in class data: " << error << endl;
            exit(1);
        }
    }

    // Method that is used to delete an item using its id.
    void deleteItem(int itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items[i].getId() == itemId) {
                items.erase(items.begin() + i);
                break;
            }
        }

        try {
            mysqlx::Session session("mysqlx://root:Simone2006@localhost:33060");
            mysqlx::SqlStatement statement = session.sql("USE InventoryManagementSystem");
            statement.execute();
            statement = session.sql("DELETE FROM Items WHERE id = ?;").bind(itemId);
            statement.execute();
        }
        catch (const mysqlx::Error &error) {
            cerr << "There was an error in delete item method in class data: " << error << endl;
            exit(1);
        }
    }

    // Method that will return a pointer to the most expensive item. It returns nullptr if the items are empty.
    Item *getMostExpensiveItem() {
        double price = 0;
        Item *result = nullptr;

        for (Item &item: items) {
            if (item.getPrice() >= price) {
                result = &item;
                price = item.getPrice();
            }
        }

        return result;
    }

    // Method that will pointer to the item with most quantity. It will return nullptr if the items are empty.
    Item *getItemWithMostQuantity() {
        int quantity = 0;
        Item *result = nullptr;

        for (Item &item: items) {
            if (item.getQuantity() >= quantity) {
                result = &item;
                quantity = item.getQuantity();
            }
        }

        return result;
    }

    // Overloaded iterators.
    std::vector<Item>::iterator begin() {
        return items.begin();
    }

    std::vector<Item>::iterator end() {
        return items.end();
    }

    [[nodiscard]] std::vector<Item>::const_iterator begin() const {
        return items.begin();
    }

    [[nodiscard]] std::vector<Item>::const_iterator end() const {
        return items.end();
    }
};

// Class that is used to create the window to add an item.
class AddItemWindow : public QWidget {
Q_OBJECT

private:
    // The main layout of the window.
    QGridLayout *layout = new QGridLayout();
    // QLineEdits used to take and display the information about the item.
    QLineEdit *nameLineEdit = new QLineEdit();
    QLineEdit *priceLineEdit = new QLineEdit();
    QLineEdit *quantityLineEdit = new QLineEdit();
public:
    explicit AddItemWindow(QWidget *parent = nullptr) : QWidget(parent) {
        // Setting attributes to the window.
        this->setWindowTitle("Add item");
        this->setGeometry(300, 300, 500, 500);
        this->setAttribute(Qt::WA_DeleteOnClose);


        // Creating the UI.
        this->setLayout(layout);
        createUI();

        // Showing the window.
        this->show();
    }

private:
    // Method used to create the UI.
    void createUI() {
        // Creating the widgets.
        auto addButton = new QPushButton("Add item");

        // Connecting the widgets.
        connect(addButton, &QPushButton::clicked, [=]() { onAddButtonClicked(); });

        // Adding the widgets.
        layout->addWidget(new QLabel("Name:"), 0, 0);
        layout->addWidget(nameLineEdit, 0, 1);
        layout->addWidget(new QLabel("Price:"), 1, 0);
        layout->addWidget(priceLineEdit, 1, 1);
        layout->addWidget(new QLabel("Quantity:"), 2, 0);
        layout->addWidget(quantityLineEdit, 2, 1);
        layout->addWidget(addButton, 3, 0);
    }

signals:

    // Signal that is emitted when the user clicks add button.
    void itemAdded(const std::string &t1, double t2, int t3);

private slots:

    // Method that is activated when the user clicks add button.
    void onAddButtonClicked() {
        string name;
        double price;
        int quantity;
        bool parsed = true;

        name = nameLineEdit->text().toStdString();

        if (name.empty()) {
            QMessageBox::warning(this, "Error", "The name can't be empty");
            return;
        }

        price = priceLineEdit->text().toDouble(&parsed);
        if (!parsed || price < 0) {
            QMessageBox::warning(this, "Error", "Please enter a valid price");
            return;
        }

        quantity = quantityLineEdit->text().toInt(&parsed);

        if (!parsed || quantity < 0) {
            QMessageBox::warning(this, "Error", "Please enter a valid quantity");
            return;
        }

        emit itemAdded(name, price, quantity);
        this->close();
    }
};

// Class that is used to create the windows with information about an item.
class ItemInfoWindow : public QWidget {
Q_OBJECT

private:
    // Variable that will hold that item that is displayed.
    const Item &item;
    // The main layout of the window.
    QGridLayout *layout = new QGridLayout();
    // QLineEdits used to take and display the information about the item.
    QLineEdit *nameLineEdit = new QLineEdit();
    QLineEdit *priceLineEdit = new QLineEdit();
    QLineEdit *quantityLineEdit = new QLineEdit();
public:
    explicit ItemInfoWindow(const Item &item, QWidget *parent = nullptr) : item(item), QWidget(parent) {
        // Setting attributes to the window.
        this->setWindowTitle("Item info");
        this->setGeometry(300, 300, 500, 500);
        this->setAttribute(Qt::WA_DeleteOnClose);

        // Creating the UI.
        this->setLayout(layout);
        createUI();

        // Showing the window.
        this->show();
    }

private:
    void createUI() {
        // Creating the widgets.
        auto saveButton = new QPushButton("Save");
        auto deleteButton = new QPushButton("Delete");

        // Setting attributes to the widgets.
        nameLineEdit->setText(QString::fromStdString(item.getName()));
        priceLineEdit->setText(QString::number(item.getPrice()));
        quantityLineEdit->setText(QString::number(item.getQuantity()));

        // Connecting the widgets.
        connect(saveButton, &QPushButton::clicked, [=]() { onSaveButtonClicked(); });
        connect(deleteButton, &QPushButton::clicked, [=]() { onDeleteButtonClicked(); });

        // Adding the widgets.
        layout->addWidget(new QLabel("Name:"), 0, 0);
        layout->addWidget(nameLineEdit, 0, 1);
        layout->addWidget(new QLabel("Price:"), 1, 0);
        layout->addWidget(priceLineEdit, 1, 1);
        layout->addWidget(new QLabel("Quantity:"), 2, 0);
        layout->addWidget(quantityLineEdit, 2, 1);
        layout->addWidget(saveButton, 3, 0);
        layout->addWidget(deleteButton, 4, 0);
    }

signals:

    void itemSaved(int _t1, const std::string &_t2, double _t3, int _t4);

    void itemDeleted(int _t1);

private slots:

    // Method called when the user clicks save button.
    void onSaveButtonClicked() {
        string name;
        double price;
        int quantity;
        bool parsed = true;

        name = nameLineEdit->text().toStdString();

        if (name.empty()) {
            QMessageBox::warning(this, "Error", "The name can't be empty");
            return;
        }

        price = priceLineEdit->text().toDouble(&parsed);
        if (!parsed || price < 0) {
            QMessageBox::warning(this, "Error", "Please enter a valid price");
            return;
        }

        quantity = quantityLineEdit->text().toInt(&parsed);

        if (!parsed || quantity < 0) {
            QMessageBox::warning(this, "Error", "Please enter a valid quantity");
            return;
        }

        emit itemSaved(item.getId(), name, price, quantity);
        this->close();
    }

    // Method called whe the user clicks delete button.
    void onDeleteButtonClicked() {
        emit itemDeleted(item.getId());
    }
};

// Class that is used to create the main window.
class MainWindow : public QMainWindow {
Q_OBJECT

private:
    Data data;
    // Search bar used to find items.
    QLineEdit *searchBar = new QLineEdit();
    // The main layout of the window.
    QGridLayout *layout = new QGridLayout();
    // Layout used for the results.
    QVBoxLayout *resultsLayout = new QVBoxLayout();


public:
    explicit MainWindow(int userId, QWidget *parent = nullptr) : data(Data(userId)), QMainWindow(parent) {
        // Setting attributes to the window.
        this->setWindowTitle("Inventory management system");
        this->setGeometry(300, 300, 500, 500);
        this->setAttribute(Qt::WA_DeleteOnClose);

        // Creating the UI.
        auto *mainWidgets = new QWidget();
        mainWidgets->setLayout(layout);
        this->setCentralWidget(mainWidgets);
        createUI();

        // Creating the menu.
        createMenu();

        // Showing the window.
        this->show();
    }

private:
    // Method that is used to create the UI.
    void createUI() {
        // Creating the widgets.
        auto frame = new QFrame();
        auto scrollArea = new QScrollArea();

        // Setting attributes to the widgets.
        frame->setLayout(resultsLayout);
        scrollArea->setWidget(frame);
        scrollArea->setWidgetResizable(true);

        // Connecting the attributes.
        connect(searchBar, &QLineEdit::returnPressed, [=]() { onSearch(); });

        // Adding the widgets.
        layout->addWidget(searchBar, 0, 0);
        layout->addWidget(scrollArea, 1, 0);
    }

    // Method that is used to create the menu.
    void createMenu() {
        // Creating the menus.
        auto itemMenu = this->menuBar()->addMenu("Item");
        auto inventoryMenu = this->menuBar()->addMenu("Inventory");

        // Creating the actions.
        auto addItemAction = new QAction("Add item", this);
        auto showTotalValueAction = new QAction("Show total value", this);
        auto showMostExpensiveItem = new QAction("Show most expensive item", this);
        auto showItemWithMostQuantity = new QAction("Show item with most quantity", this);

        // Connecting the widgets.
        connect(addItemAction, &QAction::triggered, [=]() { onAddItemClicked(); });
        connect(showTotalValueAction, &QAction::triggered, [=]() { onShowTotalValueClicked(); });
        connect(showMostExpensiveItem, &QAction::triggered, [=]() { onShowMostExpensiveItemClicked(); });
        connect(showItemWithMostQuantity, &QAction::triggered, [=]() { onShowItemWithMostQuantity(); });

        // Adding the actions.
        itemMenu->addAction(addItemAction);
        inventoryMenu->addAction(showTotalValueAction);
        inventoryMenu->addAction(showMostExpensiveItem);
        inventoryMenu->addAction(showItemWithMostQuantity);
    }

public slots:

    // Method that will be activated when the user press return in the search bar. It will display all the items with matching names.
    void onSearch() {
        QLayoutItem *layoutItem;

        while ((layoutItem = resultsLayout->takeAt(0)) != nullptr) {
            QWidget *widget = layoutItem->widget();
            if (widget != nullptr) {
                resultsLayout->removeWidget(widget);
                delete widget;
            }
            delete layoutItem;
        }

        string text = searchBar->text().trimmed().toLower().toStdString();

        for (const Item &item: data) {
            if (item.getName().find(text) != string::npos) {
                auto result = new QPushButton(QString::fromStdString(item.getName()));
                connect(result, &QPushButton::clicked, [=]() { onResultClicked(item); });
                resultsLayout->addWidget(result);
            }
        }
    }

private slots:

    // Method activated when the user clicks an item. It will display the information about the item.
    void onResultClicked(const Item &item) const {
        auto itemInfoWindow = new ItemInfoWindow(item);
        connect(itemInfoWindow, &ItemInfoWindow::itemSaved, this, &MainWindow::onItemSaved);
        connect(itemInfoWindow, &ItemInfoWindow::itemDeleted, this, &MainWindow::onItemDeleted);
    }

    // Method that will be activated when a signal itemSaved from the ItemInfoWindow is emitted.
    void onItemSaved(int id, const std::string &name, double price, int quantity) {
        for (Item &item: data) {
            if (item.getId() == id) {
                item.setName(name);
                item.setPrice(price);
                item.setQuantity(quantity);
                break;
            }
        }
        onSearch();
    }

    // Method that will be activated when a signal itemDeleted form the ItemInfoWindow is emitted.
    void onItemDeleted(int id) {
        data.deleteItem(id);
        onSearch();
    }

    // Method that will be called when the user clicks add item form the menu.
    void onAddItemClicked() const {
        auto addItemWindow = new AddItemWindow();
        connect(addItemWindow, &AddItemWindow::itemAdded, this, &MainWindow::onItemAdded);
    }

    // Method that will be activated when itemAdded signal is emitted form the AddItemWindow.
    void onItemAdded(const std::string &name, double price, int quantity) {
        data.addItem(name, price, quantity);
        onSearch();
    }

    // Method that will be activated when the user clicks show total value from the menu.
    void onShowTotalValueClicked() {
        double value = 0;

        for (const Item &item: data) {
            value += item.getPrice() * item.getQuantity();
        }

        QMessageBox::information(this, "Total Value", QString("The total value is %1").arg(value));
    }

    // Method that will be activated when the user clicks the show most expensive item from the menu.
    void onShowMostExpensiveItemClicked() {
        Item *item = data.getMostExpensiveItem();

        if (item) {
            QMessageBox::information(this, "Most expensive item", QString("The most expensive item: %1").arg(
                    QString::fromStdString(item->getName())));
        } else {
            QMessageBox::warning(this, "Error", "There aren't any items.");
        }
    }

    // Method that will be activated when the user clicks show item with most quantity.
    void onShowItemWithMostQuantity() {
        Item *item = data.getItemWithMostQuantity();

        if (item) {
            QMessageBox::information(this, "Item with most quantity", QString("The item with most quantity: %1").arg(
                    QString::fromStdString(item->getName())));
        } else {
            QMessageBox::warning(this, "Error", "There aren't any items.");
        }
    }

signals:

    // Signal that will be emitted when the main window is closed.
    void mainWindowClosed();

protected:
    // Overloaded close event.
    void closeEvent(QCloseEvent *event) override {
        emit mainWindowClosed();
        data.saveData();
        this->close();
    }
};

// Class that is used to create the login window.
class LoginWindow : public QWidget {
Q_OBJECT

private:
    // Layout used for the window.
    QGridLayout *layout = new QGridLayout();
    // QLineEdits used for getting the email and the password.
    QLineEdit *emailLineEdit = new QLineEdit();
    QLineEdit *passwordLineEdit = new QLineEdit();
public:
    explicit LoginWindow(QWidget *parent = nullptr) : QWidget(parent) {
        // Setting attributes to the window.
        this->setWindowTitle("Inventory management system");
        this->setGeometry(300, 300, 400, 400);

        // Creating the UI.
        this->setLayout(layout);
        createUI();
    }

private:
    // Method used to create the UI.
    void createUI() {
        // Creating the widgets.
        auto loginButton = new QPushButton("Login");
        auto registerButton = new QPushButton("Register");

        // Setting attributes to widgets.
        passwordLineEdit->setEchoMode(QLineEdit::Password);

        // Connecting the widgets.
        connect(loginButton, &QPushButton::clicked, [=]() { onLoginButtonClicked(); });
        connect(registerButton, &QPushButton::clicked, [=]() { onRegisterButtonClicked(); });

        // Adding the widgets.
        layout->addWidget(new QLabel("Email:"), 0, 0);
        layout->addWidget(emailLineEdit, 0, 1);
        layout->addWidget(new QLabel("Password:"), 1, 0);
        layout->addWidget(passwordLineEdit, 1, 1);
        layout->addWidget(loginButton, 2, 0);
        layout->addWidget(registerButton, 2, 1);
    }

private slots:

    // Slot that will be activated when the user clicks the login button.
    void onLoginButtonClicked() {
        string email = emailLineEdit->text().toStdString();
        string password = passwordLineEdit->text().toStdString();

        int userId = Data::Login(email, password);

        if (userId != -1) {
            auto mainWindow = new MainWindow(userId);
            connect(mainWindow, &MainWindow::mainWindowClosed, this, &LoginWindow::onMainWindowClosed);
            this->hide();
        } else {
            QMessageBox::warning(this, "Error", "The email or the password are incorrect");
        }
    }

    // Slot that will be activated when the user clicks the register button.
    void onRegisterButtonClicked() {
        string email = emailLineEdit->text().toStdString();
        string password = passwordLineEdit->text().toStdString();

        if (Data::Register(email, password)) {
            QMessageBox::information(this, "Registration successful", "You are now registered");
        } else {
            QMessageBox::warning(this, "Registration failed", "An account with the email already exists.");
        }
    }

    // Slot that will be activated when the main window is closed.
    void onMainWindowClosed() {
        emailLineEdit->setText("");
        passwordLineEdit->setText("");
        this->show();
    }
};

int main(int argc, char *argv[]) {
    QApplication application(argc, argv);
    LoginWindow loginWindow;
    loginWindow.show();
    return QApplication::exec();
}

#include "main.moc"