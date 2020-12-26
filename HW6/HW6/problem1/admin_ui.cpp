#include "admin_ui.h"

AdminUI::AdminUI(ShoppingDB &db, std::ostream& os): UI(db, os) { }

void AdminUI::add_product(std::string name, int price) {
    // TODO: For problem 1-1
    int status = db.admin_add_product(name, price);
    if (status == SUCCESS){
        os << "ADMIN_UI: " << name << " is added to the database." << std::endl;
    } else {
        os << "ADMIN_UI: Invalid price." << std::endl;
    }
}

void AdminUI::edit_product(std::string name, int price) {
    // TODO: For problem 1-1
    int status = db.admin_edit_product(name, price);
    if (status == INVALID_PRODCT_NAME) {
        os << "ADMIN_UI: Invalid product name." << std::endl;
    } else if (status == INVALID_PRICE){
        os << "ADMIN_UI: Invalid price." << std::endl;
    } else {
        os << "ADMIN_UI: " << name << " is modified from the database." << std::endl;
    }
}

void AdminUI::list_products() {
    // TODO: For problem 1-1
    os << "ADMIN_UI: Products: ";
    db.admin_list_products(os);
}
