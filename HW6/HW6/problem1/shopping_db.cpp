#include "shopping_db.h"

ShoppingDB::ShoppingDB() {

}

int ShoppingDB::admin_add_product(std::string name, int price) {
    if (price <= 0) {
        return INVALID_PRICE;
    } else {
        products.push_back(new Product(name, price));
        return SUCCESS;
    }
}

int ShoppingDB::find_product(std::string &name) {
    for (int i = 0; i < products.size(); i++){
        if (products.at(i)->name == name){
            return i;
        }
    }
    return INVALID_PRODCT_NAME;
}

int ShoppingDB::admin_edit_product(std::string name, int price) {
    int loc = find_product(name);
    if (loc == -1){
        return INVALID_PRODCT_NAME;
    } else {
        if (price <= 0){
            return INVALID_PRICE;
        }
        else {
            products.at(loc)->price = price;
            return SUCCESS;
        }
    }
}

void ShoppingDB::admin_list_products(std::ostream& os) {
    os << "[";
    for (Product* product:products){
        os << "(" << product->name << ", " << product->price << ")";
        if (product != products.back()) os << ", ";
    }
    os << "]" << std::endl;
}

void ShoppingDB::user_sign_up(std::string &name, std::string &password, bool premium) {
    User* user;
    if (premium){
        user = new PremiumUser(name, password);
    } else {
        user = new NormalUser(name, password);
    }
    users.push_back(user);
}

int ShoppingDB::find_user(std::string &username) {
    for (int i = 0; i < users.size(); i++){
        if (users.at(i)->name == username){
            return i;
        }
    }
    return -1;
}

User* ShoppingDB::user_log_in(std::string &name, std::string &password) {
    int loc = find_user(name);
    if (loc == -1){
        return nullptr;
    } else {
        User* user = users.at(loc);
        if (user->log_in(password)) return user;
        else return nullptr;
    }
}

int ShoppingDB::user_buy(std::string &product_name) {
    int loc = find_product(product_name);
    if (loc == -1){
        return INVALID_PRODCT_NAME;
    } else {
        return products.at(loc)->price;
    }
}
typedef std::pair<int, int> pairInt;
struct comparePair {
    bool operator()(pairInt &lhs, pairInt &rhs) {
        return (lhs.first == rhs.first) ? (lhs.second > rhs.second) : (lhs.first < rhs.first);
    };
};


std::vector<std::string> ShoppingDB::recommendation_premium(User *user) {

    std::priority_queue<pairInt, std::vector<pairInt>, comparePair> pqueue;
    for (User* user2:users){
        if (user != user2){
            pqueue.push(std::pair<int, int>(similarity(user, user2), user2->registerID));
        }
    }

    std::vector<std::string> recommends;
    int cnt = 0;
    std::set<std::pair<int, std::string>> set;
    while (cnt < 3 && !pqueue.empty()){
        auto pair = pqueue.top();
        pqueue.pop();
        std::string product = users.at(pair.second)->purchaseHistory.back();
        bool overlap = false;
        for (std::string rec:recommends){
            if (rec == product) overlap = true;
        }
        if (!overlap) recommends.push_back(product);
    }


    return recommends;
}

int ShoppingDB::similarity(User *user1, User *user2) {
    std::vector<std::string> &bigger = (user1->purchaseHistory.size() > user2->purchaseHistory.size()) ? user1->purchaseHistory : user2 ->purchaseHistory;
    std::vector<std::string> &smaller = (user1->purchaseHistory.size() > user2->purchaseHistory.size()) ? user2->purchaseHistory : user1 ->purchaseHistory;
    std::set<std::string> set;
    int cnt = 0;
    for (std::string product:bigger){
        if (std::find(smaller.begin(), smaller.end(), product) != smaller.end()){
            if (set.insert(product).second) cnt += 1;
        }
    }
    return cnt;
}

std::vector<std::string> ShoppingDB::recommendation_normal(User *user) {
    std::vector<std::string> recommendation;
    int maxSize = (user->purchaseHistory.size() > 3) ? 3 : user->purchaseHistory.size();
    int idx = user->purchaseHistory.size();
    while (maxSize > 0 && idx >= 1){
        std::string product = user->purchaseHistory.at(--idx);
        bool overlap = false;
        for (std::string rec:recommendation){
            if (rec == product) overlap = true;
        }
        if (!overlap) {
            recommendation.push_back(product);
            maxSize--;
        }

    }
    return recommendation;
}












