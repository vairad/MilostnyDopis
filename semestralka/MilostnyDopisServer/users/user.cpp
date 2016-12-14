#include "user.h"

#include <algorithm>
#include <functional>
#include <cctype>
#include <locale>

// trim from start
static inline std::string *ltrim(std::string *s) {
    s->erase(s->begin(), std::find_if(s->begin(), s->end(),
            std::not1(std::ptr_fun<int, int>(std::isspace))));
    return s;
}

// trim from end
static inline std::string *rtrim(std::string *s) {
    s->erase(std::find_if(s->rbegin(), s->rend(),
            std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s->end());
    return s;
}


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

const char *User::toString()
{
    std::string tmp;
    tmp = "";
    tmp += *nickname;
    tmp += " ID: ";
    tmp += uid;
    return tmp.c_str();
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
    this->nickname = ltrim(rtrim(nickname));
    this->game = NULL;
}

void User::setUID(std::string uid)
{
    this->uid = uid;
}


