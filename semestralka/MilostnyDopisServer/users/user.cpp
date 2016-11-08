#include "user.h"

std::string User::getUID() const
{
    return uid;
}

int User::getSocket() const
{
    return socket;
}

std::string *User::getNickname() const
{
    return nickname;
}

User::User(std::string *nickname, int socket) :
    nickname(nickname)
                                      , socket(socket)
{

}

void User::setUID(std::string uid)
{
    this->uid = uid;
}
