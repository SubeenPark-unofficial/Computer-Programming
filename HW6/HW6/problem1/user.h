#ifndef PROBLEM1_USER_H
#define PROBLEM1_USER_H

#include <string>
#include <vector>
#include <iostream>
#include "product.h"

class User {

public:
    User(std::string name, std::string password);
    const std::string name;
    int registerID;
    bool premium;
    int static userCount;
    bool log_in(std::string password);
    std::vector<std::string> purchaseHistory;
    void purchase(std::string productName);
private:
    std::string password;
};

class NormalUser : public User {
public:
    NormalUser(const std::string &name, const std::string &password);

};

class PremiumUser : public User {
public:
    PremiumUser(const std::string &name, const std::string &password);

};

#endif //PROBLEM1_USER_H
