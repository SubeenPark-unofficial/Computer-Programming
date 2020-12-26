#include <iostream>
#include <utility>
#include <istream>
#include <ostream>
#include <algorithm>
#include <string>
#include <filesystem>
#include <fstream>
#include <list>
#include <sstream>
#include <ctime>
#include <vector>
#include "config.h"
#include "app.h"

typedef std::pair<std::string, std::string> IDandPW;
typedef std::string Command;
typedef std::pair<std::string, std::list<std::string>> TitleAndContent;


class User{
private:
    std::string id;
public:
    User(const std::string &id, const std::string &password);
    bool authenticate(const std::string &password);
    const std::string &getId() const;

private:
    std::string password;
public:
    const std::string &getPassword() const;
};

class Post{
public:


    std::string title;
    std::list<std::string> content;
    int id;
    std::string user;
    time_t dateTime;
    std::string timeString;

    Post(const std::string &title, const std::list<std::string> &content, int id, const std::string &user,
         const std::string &timeString);

    Post(const std::string &title, const std::list<std::string> &content, int id, const std::string &user,
         time_t dateTime);

    std::string dateTimeToString();
    void stringToDateTime(const std::string& dateString);
    void writePost();
    void recommendPost(std::ostream &os);
    void searchedPost(std::ostream &os);

};


class DataBase{
private:
    std::list<User> users;
    std::list<Post> posts;
public:
    void retrieveUsers();
    void retrievePosts();
    void writePost(const TitleAndContent& titleAndContent, std::string user);
    User findUser(const std::string& id);
    static std::list<std::string> retrieveFriends(const std::string& user);
    static std::vector<Post> retrieveFriendPosts(const std::string& user);
    std::vector<Post> recommendFriendPost(const std::string& user);
    std::vector<std::pair<Post, int>> countKeywords(const std::vector<std::string> keywords);
    std::vector<Post> searchPost(const std::vector<std::string> keywords);
    int postCount = 0;
};



class Display{
public:
    static IDandPW authView(std::istream &is, std::ostream &os);
    static Command menuView(std::istream &is, std::ostream &os, const std::string& accountOwner);
    static TitleAndContent postView(std::istream &is, std::ostream &os);
    static void recommendView(std::ostream &os, const std::vector<Post>& posts);
    static void searchView(std::ostream &os, const std::vector<Post>& posts);


};

namespace fs = std::filesystem;

class UserInterface{
public:
    UserInterface(std::istream &is, std::ostream &os);
    std::istream& is;
    std::ostream& os;
    void menu();
private:
    DataBase dataBase;
    Display display;

};

App::App(std::istream& is, std::ostream& os): is(is), os(os) {
    // TODO

}

void App::run() {
    // TODO
    UserInterface ui(is, os);
    ui.menu();
}



const std::string &User::getId() const {
    return id;
}

User::User(const std::string &id, const std::string &password) : id(id), password(password) {}

bool User::authenticate(const std::string &password) {
    return (this->password == password);
}

const std::string &User::getPassword() const {
    return password;
}







std::string Post::dateTimeToString() {
    char buff[20];
    std::strftime(buff, 20, "%Y/%m/%d %H:%M:%S", localtime(&dateTime));
    return std::string(buff);
}

void Post::stringToDateTime(const std::string& dateString) {
    time_t tStart;
    int yy, month, dd, hh, mm, ss;
    struct tm whenStart;
    const char *zStart = dateString.c_str();

    sscanf(zStart, "%d/%d/%d %d:%d:%d", &yy, &month, &dd, &hh, &mm, &ss);
    whenStart.tm_year = yy - 1900;
    whenStart.tm_mon = month - 1;
    whenStart.tm_mday = dd;
    whenStart.tm_hour = hh;
    whenStart.tm_min = mm;
    whenStart.tm_sec = ss;
    whenStart.tm_isdst = -1;

    tStart = mktime(&whenStart);
    dateTime = tStart;

}

void Post::writePost() {
    std::string path = SERVER_STORAGE_DIR;
    std::ofstream ofs(path + user + "/post/" + std::to_string(id) + ".txt");
    ofs << timeString << std::endl;
    ofs << title << std::endl;
    ofs << std::endl;
    int count = 0;
    for (const std::string& line:content){
        ofs << line;
        if (count != content.size()-1) ofs << std::endl;
        count++;
    }
    ofs.close();
}

void Post::recommendPost(std::ostream &os) {
    os << "-----------------------------------" << std::endl;
    os << "id: " << id << std::endl;
    os << "created at: " << timeString << std::endl;
    os << "title: " << title << std::endl;
    os << "content: " << std::endl;
    for (const std::string& line:content){
        os << line << std::endl;
    }
}

Post::Post(const std::string &title, const std::list<std::string> &content, int id, const std::string &user,
           const std::string &timeString) : title(title), content(content), id(id), user(user),
                                            timeString(timeString) {}

Post::Post(const std::string &title, const std::list<std::string> &content, int id, const std::string &user,
           time_t dateTime) : title(title), content(content), id(id), user(user), dateTime(dateTime) {
    timeString = dateTimeToString();
}

void Post::searchedPost(std::ostream &os) {
    os << "id: " << id << ", created at: " << timeString << ", title: " << title << std::endl;
}


void DataBase::retrieveUsers() {
    std::string path = SERVER_STORAGE_DIR;
    std::list<std::string> dirList;
    for (const auto &entry : fs::directory_iterator(path)){
        dirList.push_back(entry.path().string());
    }
    std::list<User> userList;
    for (const std::string& userPath:dirList){
        std::string id = fs::path(userPath).filename().string();
        for (const auto &entry: fs::directory_iterator(userPath)){
            if (entry.path().filename().string() == "password.txt"){
                std::ifstream ifs(entry.path().string());
                std::string password((std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>());
                User user(id, password);
                userList.push_back(user);
                ifs.close();
            }
        }
    }
    users = userList;
}




void DataBase::retrievePosts() {
    int cnt = 0;
    std::string path = SERVER_STORAGE_DIR;
    std::list<std::string> dirList;
    for (const auto &entry : fs::directory_iterator(path)){
        dirList.push_back(entry.path().string());
    }
    std::list<Post> postLists;
    for (const std::string& userDir :dirList){
        fs::path postDir = fs::path(userDir + "/post/");
        std::string user = fs::path(userDir).stem();
        if (fs::exists(postDir)){
            for (const auto &entry : fs::directory_iterator(postDir.string())){
                int id = std::stoi(entry.path().stem().string());
                std::string dateTimeString, title, dummy;
                std::list<std::string> content;
                std::ifstream ifs(entry.path().string());
                getline(ifs, dateTimeString);
                getline(ifs, title);
                getline(ifs, dummy);
                while (getline(ifs, dummy)){
                    content.push_back(dummy);
                }
                Post post(title, content, id, user, dateTimeString);
                cnt++;
                postLists.push_back(post);
                ifs.close();
            }
        }
    }
    posts = postLists;
    postCount = cnt;
}

void DataBase::writePost(const TitleAndContent& titleAndContent, std::string user) {
    retrievePosts();
    Post post(titleAndContent.first, titleAndContent.second, postCount++, user,time(nullptr));
    post.writePost();
}

User DataBase::findUser(const std::string& id) {
    retrieveUsers();
    for (User user:users){
        if (user.getId() == id) return user;
    }
    return User("","");
}

std::list<std::string> DataBase::retrieveFriends(const std::string& user) {
    std::string path = SERVER_STORAGE_DIR;
    std::list<std::string> friends;
    std::fstream ifs(path + user + "/friend.txt");
    std::string line;
    while (getline(ifs, line)){
        if (!line.empty()) friends.push_back(line);
    }
    ifs.close();
    return friends;
}

std::vector<Post> DataBase::retrieveFriendPosts(const std::string& user) {
    std::list<std::string> friends = retrieveFriends(user);
    std::vector<Post> friendPosts;

    std::string path = SERVER_STORAGE_DIR;
    for (std::string friendID : friends){
        std::string friendPath = path + friendID + "/post/";
        for (const auto &entry : fs::directory_iterator(friendPath)){
            std::fstream ifs(entry.path().string());
            int id = std::stoi(entry.path().stem().string());
            std::string dateTimeString, title, dummy;
            std::list<std::string> content;
            getline(ifs, dateTimeString);

            getline(ifs, title);
            getline(ifs, dummy);
            while (getline(ifs, dummy)){
                content.push_back(dummy);
            }
            Post post(title, content, id, user, dateTimeString);
            friendPosts.push_back(post);
            ifs.close();
        }
    }
    return friendPosts;
}

std::vector<Post> DataBase::recommendFriendPost(const std::string& user){
    std::vector<Post> friendPosts = retrieveFriendPosts(user);
    std::vector<Post> recommendPosts;
    auto compare = [](const Post &a, const Post &b) {
        return a.timeString > b.timeString;};
    std::sort(std::begin(friendPosts), std::end(friendPosts), compare);
    int cnt = (friendPosts.size() >= 10) ? 10 : friendPosts.size();
    for (int i = 0; i < cnt; i++){
        recommendPosts.push_back(friendPosts.at(i));
    }
    return recommendPosts;
}

std::vector<Post> DataBase::searchPost(const std::vector<std::string> keywords) {
    std::vector<Post> searchResult;
    std::vector<std::pair<Post, int>> countRes = countKeywords(keywords);
    auto compare = [](const std::pair<Post, int> &a, const std::pair<Post, int> &b) {
        return ((a.second == b.second) ? a.first.timeString > b.first.timeString : a.second > b.second);};
    std::sort(std::begin(countRes), std::end(countRes), compare);
    int cnt = (countRes.size() >= 10) ? 10 : countRes.size();
    for (int i = 0; i < cnt; i++){
        if (countRes.at(i).second == 0) break;
        searchResult.push_back(countRes.at(i).first);
    }
    return searchResult;
}


std::vector<std::string> split(std::string input, char delimiter) {
    std::istringstream ss(input);
    std::string stringBuffer;
    std::vector<std::string> x;
    x.clear();
    while (getline(ss, stringBuffer, ' ')){
        x.push_back(stringBuffer);
    }
    return x;
}

std::vector<std::pair<Post, int>> DataBase::countKeywords(const std::vector<std::string> keywords) {
    retrievePosts();
    std::vector<std::pair<Post, int>> countRes;

    for (Post post:posts){
        int cnt = 0;
        std::vector<std::string> v1 = split(post.title, ' ');
        for (std::string line:post.content){
            std::vector<std::string> v2 = split(line, ' ');
            v1.insert(v1.end(), v2.begin(), v2.end());
        }


        for (std::string keyword:keywords){
            for (std::string word:v1){
                if (word == keyword) cnt += 1;
            }
        }
        countRes.push_back(std::pair<Post, int>(post, cnt));
    }

    return countRes;
}




IDandPW Display::authView(std::istream &is, std::ostream &os) {
    std::string id;
    std::string password;
    os << "------ Authentication ------" << std::endl;
    os << "id=";
    is >> id;
    os << "passwd=";
    is >> password;
    return IDandPW(id, password);
}

Command Display::menuView(std::istream &is, std::ostream &os, const std::string& accountOwner) {
    os << "-----------------------------------" << std::endl;
    os << accountOwner << "@sns.com" << std::endl;
    os << "post : Post contents" << std::endl;
    os << "recommend : recommend interesting posts" << std::endl;
    os << "search <keyword> : List post entries whose contents contain <keyword>" << std::endl;
    os << "exit : Terminate this program" << std::endl;
    os << "-----------------------------------" << std::endl;
    os << "Command=";
    std::string command;
    is.ignore();
    getline(is, command);
    os << std::endl;
    return command;
}

TitleAndContent Display::postView(std::istream &is, std::ostream &os) {
    std::string title;
    std::list<std::string> content;
    os << "-----------------------------------" << std::endl;
    os << "New Post" << std::endl;
    os << "* Title=";
    is.ignore();
    getline(is, title);
    os <<"* Content" << std::endl;
    std::string line;
    os << ">";
    std::getline(is, line);
    while (!line.empty()){
        content.push_back(line);
        os << ">";
        std::getline(is, line);
    }
    return TitleAndContent(title, content);
}

void Display::recommendView(std::ostream &os, const std::vector<Post>& posts) {
    for (Post post:posts) post.recommendPost(os);
}

void Display::searchView(std::ostream &os, const std::vector<Post> &posts) {
    os << "-----------------------------------" << std::endl;
    for (Post post:posts){
        post.searchedPost(os);
    }

}

UserInterface::UserInterface(std::istream &is, std::ostream &os) : is(is), os(os) {}

void UserInterface::menu() {
    IDandPW pair = Display::authView(is, os);
    User user = dataBase.findUser(pair.first);
    if (user.getId().empty() && user.getPassword().empty()) return;
    if (user.authenticate(pair.second)){
        Command command = Display::menuView(is, os, user.getId());


        while (command != "exit"){
            if (command == "post"){
                TitleAndContent titleAndContent = Display::postView(is, os);
                dataBase.writePost(titleAndContent, user.getId());
                command = Display::menuView(is, os, user.getId());
            }
            if (command == "recommend"){
                std::vector<Post> recommended = dataBase.recommendFriendPost(user.getId());
                Display::recommendView(os, recommended);
                command = Display::menuView(is,os, user.getId());
            }
            std::vector<std::string> keywords;
            if (command.rfind("search", 0) == 0){
                std::vector<std::string> result = split(command, ' ');
                if (result.size() >= 1){
                    keywords = result;
                    keywords.erase(keywords.begin());
                    keywords.erase(std::unique(keywords.begin(), keywords.end()), keywords.end());
                }
                std::vector<Post> searchResults = dataBase.searchPost(keywords);
                display.searchView(os, searchResults);
                command = Display::menuView(is, os, user.getId());
            }
            if (command == "exit"){
                return;
            } else {
                return;
            }
        }


    } else {
        os << "Failed Authentication.";
    }

}