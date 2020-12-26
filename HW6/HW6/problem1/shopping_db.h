#ifndef PROBLEM1_SHOPPING_DB_H
#define PROBLEM1_SHOPPING_DB_H



const int INVALID_PRICE = -1;
const int INVALID_PRODCT_NAME = -2;
const int SUCCESS = 0;

#include <string>
#include <vector>
#include <iostream>
#include <map>
#include <queue>
#include <algorithm>
#include <cmath>
#include <set>
#include "user.h"
#include "product.h"

class ShoppingDB {
public:
    ShoppingDB();
    int admin_add_product(std::string name, int price);
    int find_product(std::string &name);
    int admin_edit_product(std::string name, int price);
    void admin_list_products(std::ostream& os);
    void user_sign_up(std::string &name, std::string &password, bool premium);
    int find_user(std::string &username);
    User* user_log_in(std::string &name, std::string &password);
    int user_buy(std::string &product_name);
    std::vector<std::string> recommendation_normal(User* user);
    std::vector<std::string> recommendation_premium(User* user);
    int similarity(User* user1, User* user2);
private:
    std::vector<User*> users;
    std::vector<Product*> products;
};



#endif //PROBLEM1_SHOPPING_DB_H
