#include "user.h"

#include "util/utilities.h"


//======================================================================================

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

void User::setSocket(int value)
{
    socket = value;
}

std::string User::toString()
{
    std::string tmp;
    tmp = "";
    tmp += *nickname;
    tmp += " ID: ";
    tmp += uid;
    return tmp;
}

std::string User::toNet()
{
    std::string tmp;
    tmp = "";
    tmp += *nickname;
    tmp += "&&";
    tmp += uid;
    return tmp;
}

Game *User::getGame() const
{
    return game;
}

void User::setGame(Game *value)
{
    game = value;
}

User::User(std::string *nickname, int socket) :
    socket(socket)
{
    this->nickname = Utilities::trim(nickname);
    this->game = NULL;
}

void User::setUID(std::string uid)
{
    this->uid = uid;
}


