#include <vector>
#include <QFile>
#include <QByteArray>
#include <QString>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QJsonValueConstRef>

#include <QApplication>
#include <QWidget>
#include <QGridLayout>
#include <QLabel>
#include <QLineEdit>
#include <QPushButton>
#include <QMessageBox>

class Item
{
private:
    QString name;
    double price;
    int quantity;
    int id;

public:
    Item(QString name, double price, int quantity, int id) : name(std::move(name)), price(price), quantity(quantity),
                                                             id(id) {}

    [[nodiscard]] QString getName() const
    {
        return name;
    }

    [[nodiscard]] double getPrice() const
    {
        return price;
    }

    void setPrice(double _price)
    {
        Item::price = _price;
    }

    [[nodiscard]] int getQuantity() const
    {
        return quantity;
    }

    void setQuantity(int _quantity)
    {
        Item::quantity = _quantity;
    }

    [[nodiscard]] int getId() const
    {
        return id;
    }

    void setId(int _id)
    {
        Item::id = _id;
    }
};

class Data
{
private:
    std::vector<Item> items;

public:
    Data()
    {
        readData();
        writeData();
    }

private:
    void readData()
    {
        QFile file("/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/c++/data.json");
        if (!file.open(QIODevice::ReadOnly))
        {
            qDebug() << "The file couldn't be opened for reading";
            exit(1);
        }

        QByteArray byteArray = file.readAll();
        file.close();

        QJsonDocument jsonDocument = QJsonDocument::fromJson(byteArray);
        QJsonArray jsonArray = jsonDocument.array();

        for (const QJsonValueRef &value : jsonArray)
        {
            QJsonObject object = value.toObject();
            QString name = object.value("name").toString();
            double price = object.value("price").toDouble();
            int quantity = object.value("quantity").toInt();
            int id = object.value("id").toInt();
            items.emplace_back(name, price, quantity, id);
        }
    }

public:
    void writeData()
    {
        revalidateIds();
        QJsonArray jsonArray;

        for (const Item &item : items)
        {
            QJsonObject object;
            object["name"] = item.getName();
            object["price"] = item.getPrice();
            object["quantity"] = item.getQuantity();
            object["id"] = item.getId();
            jsonArray.append(object);
        }

        QJsonDocument jsonDocument(jsonArray);
        QByteArray byteArray = jsonDocument.toJson();

        QFile file("/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/c++/data.json");
        if (!file.open(QIODevice::WriteOnly | QIODevice::Text))
        {
            qDebug() << "The file couldn't be opened for writing data data";
            exit(1);
        }

        file.write(byteArray);
        file.close();
    }

private:
    void revalidateIds()
    {
        for (int i = 0; i < items.size(); i++)
        {
            items[i].setId(i);
        }
    }

public:
    void deleteItem(int id)
    {
        for (int i = 0; i < items.size(); i++)
        {
            if (items[i].getId() == id)
            {
                items.erase(items.begin() + i);
                break;
            }
        }
    }

    void changeItemValues(double price, int quantity, int id)
    {
        for (Item &item : items)
        {
            if (item.getId() == id)
            {
                item.setPrice(price);
                item.setQuantity(quantity);
                break;
            }
        }
    }

    void addItem(const QString &name, double price, int quantity)
    {
        items.emplace_back(name, price, quantity, getNewId());
    }

    bool checkIfNameExist(const QString &name)
    {
        for (const Item &item : items)
        {
            if (item.getName() == name)
            {
                return true;
            }
        }
        return false;
    }

private:
    int getNewId()
    {
        if (items.empty())
        {
            return 0;
        }
        else
        {
            return items.back().getId() + 1;
        }
    }

public:
    std::vector<Item>::const_iterator begin()
    {
        return items.begin();
    }

    std::vector<Item>::const_iterator end()
    {
        return items.end();
    }
};

class NewItemWindow : public QWidget
{
    Q_OBJECT

private:
    Data data;
    QLineEdit *nameLineEdit;
    QLineEdit *priceLineEdit;
    QLineEdit *quantityLineEdit;

public:
    explicit NewItemWindow(Data data, QWidget *parent = nullptr) : QWidget(parent), data(std::move(data))
    {
        // setting attributes
        this->setGeometry(250, 250, 300, 300);
        this->setWindowTitle("New Item");
        this->setAttribute(Qt::WA_DeleteOnClose);

        // creating the widgets
        auto *layout = new QGridLayout();
        nameLineEdit = new QLineEdit();
        priceLineEdit = new QLineEdit();
        quantityLineEdit = new QLineEdit();
        auto addButton = new QPushButton("Add item");

        // connecting the widgets
        connect(addButton, &QPushButton::clicked, [=]()
                { onAddButtonClicked(); });

        // adding the widgets
        layout->addWidget(new QLabel("Name :"), 0, 0);
        layout->addWidget(nameLineEdit, 0, 1);
        layout->addWidget(new QLabel("Price :"), 1, 0);
        layout->addWidget(priceLineEdit, 1, 1);
        layout->addWidget(new QLabel("Quantity"), 2, 0);
        layout->addWidget(quantityLineEdit, 2, 1);
        layout->addWidget(addButton, 3, 0);

        this->setLayout(layout);
        this->show();
    }

signals:

    void addButtonClicked(const QString &name, double price, int quantity);

private slots:

    void onAddButtonClicked()
    {
        QString name;
        double price;
        int quantity;
        bool parsed;

        name = nameLineEdit->text();
        if (data.checkIfNameExist(name))
        {
            nameLineEdit->setText("");
            QMessageBox::warning(this, "Error", "The item exist already");
            return;
        }

        price = priceLineEdit->text().toDouble(&parsed);
        if (!parsed)
        {
            priceLineEdit->setText("");
            QMessageBox::warning(this, "Error", "Invalid value in price");
            return;
        }

        quantity = quantityLineEdit->text().toInt(&parsed);
        if (!parsed)
        {
            quantityLineEdit->setText("");
            QMessageBox::warning(this, "Error", "Invalid value in quantity");
            return;
        }

        emit addButtonClicked(name, price, quantity);
        this->close();
    };
};

class ItemInfoWindow : public QWidget
{
    Q_OBJECT

    QLineEdit *priceLineEdit;
    QLineEdit *quantityLineEdit;
    int id;

public:
    explicit ItemInfoWindow(const Item &item, QWidget *parent = nullptr) : QWidget(parent)
    {
        // setting attributes to the window
        this->setGeometry(250, 250, 300, 300);
        this->setWindowTitle(item.getName());
        this->setAttribute(Qt::WA_DeleteOnClose);

        // passing teh arguments
        this->id = item.getId();

        // creating the widgets
        auto *layout = new QGridLayout();
        priceLineEdit = new QLineEdit();
        quantityLineEdit = new QLineEdit();
        auto saveButton = new QPushButton("Save");
        auto deleteButton = new QPushButton("Delete");

        // connecting the widgets and setting attributes
        priceLineEdit->setText(QString::number(item.getPrice()));
        quantityLineEdit->setText(QString::number(item.getQuantity()));
        connect(saveButton, &QPushButton::clicked, [=]()
                { onSaveButtonClicked(); });
        connect(deleteButton, &QPushButton::clicked, [=]()
                { onDeleteButtonClicked(); });

        // adding the widgets
        layout->addWidget(new QLabel("Price :"), 0, 0);
        layout->addWidget(priceLineEdit, 0, 1);
        layout->addWidget(new QLabel("Quantity :"), 1, 0);
        layout->addWidget(quantityLineEdit, 1, 1);
        layout->addWidget(saveButton, 2, 0);
        layout->addWidget(deleteButton, 3, 0);

        this->setLayout(layout);
        this->show();
    }

signals:

    void saveButtonClicked(double price, int quantity, int id);

    void deleteButtonClicked(int id);

private slots:

    void onSaveButtonClicked()
    {
        bool parsed = false;
        double price;
        int quantity;

        price = priceLineEdit->text().toDouble(&parsed);
        if (!parsed)
        {
            priceLineEdit->setText("");
            QMessageBox::warning(this, "Error", "The value in price is invalid");
            return;
        }
        quantity = quantityLineEdit->text().toInt(&parsed);
        if (!parsed)
        {
            quantityLineEdit->setText("");
            QMessageBox::warning(this, "Error", "The value in quantity is invalid");
            return;
        }

        emit saveButtonClicked(price, quantity, id);
        this->close();
    }

    void onDeleteButtonClicked()
    {
        emit deleteButtonClicked(id);
        this->close();
    }
};

class Window : public QWidget
{
    Q_OBJECT

private:
    Data data;
    QGridLayout *layout;
    std::vector<QPushButton *> results;
    QLineEdit *searchBar;

public:
    explicit Window(QWidget *parent = nullptr) : QWidget(parent)
    {
        // setting attributes to the window;
        this->setGeometry(300, 300, 500, 500);
        this->setWindowTitle("Inventory management system");

        // creating the widgets
        layout = new QGridLayout();
        auto addWindowButton = new QPushButton("Add item");
        searchBar = new QLineEdit();

        // connecting the widgets
        connect(addWindowButton, &QPushButton::clicked, [=]()
                { onAddWindowButtonClicked(); });
        connect(searchBar, &QLineEdit::returnPressed, [=]()
                { onSearch(); });

        // adding the widgets
        layout->addWidget(addWindowButton, 0, 0);
        layout->addWidget(searchBar, 1, 0);

        this->setLayout(layout);
    }

public slots:

    void onSearch()
    {
        for (QPushButton *result : results)
        {
            layout->removeWidget(result);
            delete result;
        }
        results.clear();

        QString text = searchBar->text();
        int row = 2;

        for (const Item &item : data)
        {
            if (item.getName().startsWith(text))
            {
                auto *result = new QPushButton(item.getName());
                results.push_back(result);

                connect(result, &QPushButton::clicked, [=]()
                        { onResultClicked(item); });

                layout->addWidget(result, row, 0);
                row++;
            }
        }
    };
private slots:

    void onAddWindowButtonClicked()
    {
        auto newItemWindow = new NewItemWindow(data);
        connect(newItemWindow, &NewItemWindow::addButtonClicked, this, &Window::onAddButtonClicked);
    }

    void onAddButtonClicked(const QString &name, double price, int quantity)
    {
        data.addItem(name, price, quantity);
        onSearch();
    }

    void onResultClicked(const Item &item) const
    {
        auto itemWindowInfo = new ItemInfoWindow(item);
        connect(itemWindowInfo, &ItemInfoWindow::saveButtonClicked, this, &Window::onSaveButtonClicked);
        connect(itemWindowInfo, &ItemInfoWindow::deleteButtonClicked, this, &Window::onDeleteButtonClicked);
    }

    void onSaveButtonClicked(double price, int quantity, int id)
    {
        data.changeItemValues(price, quantity, id);
        onSearch();
    }

    void onDeleteButtonClicked(int id)
    {
        data.deleteItem(id);
        onSearch();
    }
    void closeEvent(QCloseEvent *event) override
    {
        data.writeData();
        QWidget::closeEvent(event);
    }
};

int main(int argc, char *argv[])
{
    QApplication application(argc, argv);
    Window window;
    window.show();
    return QApplication::exec();
}

#include "main.moc"