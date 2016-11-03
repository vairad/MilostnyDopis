#ifndef USER_H
#define USER_H

#include <string>

class User
{
    std::string *nickname;
    int socket;
    std::string uid;
   // Game *game;
public:
    User(std::string *nickname, int socket);
    void setUID(std::string uid);
};

#endif // USER_H
