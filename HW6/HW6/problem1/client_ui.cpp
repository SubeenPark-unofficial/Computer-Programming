#include <vector>
#include "client_ui.h"
#include "product.h"
#include "user.h"

ClientUI::ClientUI(ShoppingDB &db, std::ostream& os) : UI(db, os), current_user() { }

ClientUI::~ClientUI() {
    delete current_user;
}

void ClientUI::signup(std::string username, std::string password, bool premium) {
    // TODO: For problem 1-2
    db.user_sign_up(username, password, premium);
    os << "CLIENT_UI: "<< username << " is signed up." << std::endl;
}

void ClientUI::login(std::string username, std::string password) {
    // TODO: For problem 1-2
    if (current_user != nullptr){
        os << "CLIENT_UI: Please logout first." << std::endl;
    } else {
        User* user = db.user_log_in(username, password);
        if (user == nullptr){
            os << "CLIENT_UI: Invalid username or password." << std::endl;
        } else {
            os << "CLIENT_UI: " << username << " is logged in." << std::endl;
            current_user = user;
        }
    }

}

void ClientUI::logout() {
    // TODO: For problem 1-2
    if (current_user == nullptr){
        os << "CLIENT_UI: There is no logged-in user." << std::endl;
    } else {
        os << "CLIENT_UI: " << current_user ->name << " is logged out." << std::endl;
        current_user = nullptr;
    }
}

void ClientUI::add_to_cart(std::string product_name) {
    // TODO: For problem 1-2
    if (current_user == nullptr){
        os << "CLIENT_UI: Please login first." << std::endl;
        return;
    } else {
        int loc = db.find_product(product_name);
        if (loc == INVALID_PRODCT_NAME){
            os << "CLIENT_UI: Invalid product name." << std::endl;
        } else {
            cart.push_back(product_name);
            os << "CLIENT_UI: " << product_name << " is added to the cart." << std::endl;
        }
    }

}

void ClientUI::list_cart_products() {
    // TODO: For problem 1-2.
    if (current_user == nullptr){
        os << "CLIENT_UI: Please login first." << std::endl;
        return;
    } else {
        os << "CLIENT_UI: Cart: ";
        os << "[";
        for (std::string product:cart){
            os << "(" << product << ", ";
            if (current_user->premium) os << std::round(db.user_buy(product)*0.9);
            else os << db.user_buy(product);
            os << ")";
            if (product != cart.back()) os << ", ";
        }
        os << "]" << std::endl;
    }
}

void ClientUI::buy_all_in_cart() {
    // TODO: For problem 1-2
    if (current_user == nullptr){
        os << "CLIENT_UI: Please login first." << std::endl;
        return;
    } else {
        int price = 0;
        for (std::string product:cart){
            if (current_user->premium) price += std::round(db.user_buy(product)*0.9);
            else price += db.user_buy(product);
        }
        os << "CLIENT_UI: Cart purchase completed. Total price: "<< price << "." << std::endl;
        for (std::string product:cart){
            current_user->purchase(product);
        }
        cart.clear();
    }
}

void ClientUI::buy(std::string product_name) {
    // TODO: For problem 1-2
    if (current_user == nullptr){
        os << "CLIENT_UI: Please login first." << std::endl;
        return;
    } else {
        int price = db.user_buy(product_name);
        if (price == INVALID_PRODCT_NAME){
            os << "CLIENT_UI: Invalid product name.";
        } else {
            os << "CLIENT_UI: Purchase completed. Price: "
            << ((current_user->premium) ? std::round(0.9*price) : price) << "." << std::endl;
            current_user->purchase(product_name);
        }
    }
}

void ClientUI::recommend_products() {
    // TODO: For problem 1-3.
    if (current_user == nullptr){
        os << "CLIENT_UI: Please login first." << std::endl;
        return;
    } else {
        std::vector<std::string> recommendation;
        os << "CLIENT_UI: Recommended products: [";
        if (current_user->premium){
            recommendation = db.recommendation_premium(current_user);
            for (std::string product:recommendation){
                os << "(" << product << ", " << std::round(db.user_buy(product)*0.9) << ")";
                if (product != recommendation.back()) os << ", ";
            }
        } else {
            recommendation = db.recommendation_normal(current_user);
            for (std::string product:recommendation){
                os << "(" << product << ", " << db.user_buy(product) << ")";
                if (product != recommendation.back()) os << ", ";
            }
        }
        os << "]" << std::endl;
    }

}
