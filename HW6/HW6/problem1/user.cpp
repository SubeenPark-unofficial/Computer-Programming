#include "user.h"
int User::userCount = 0;

User::User(std::string name, std::string password): name(name), password(password), registerID(userCount++) {
}

bool User::log_in(std::string password) {
    if (this->password == password) return true;
    else return false;
}

void User::purchase(std::string productName) {
    purchaseHistory.push_back(productName);
}

NormalUser::NormalUser(const std::string &name, const std::string &password) : User(name, password) {premium = false;}

PremiumUser::PremiumUser(const std::string &name, const std::string &password) : User(name, password) {premium = true;}
