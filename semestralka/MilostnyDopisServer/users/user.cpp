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

User::User(std::string *nickname, int socket) :
    socket(socket)
{
    this->nickname = ltrim(rtrim(nickname));
}

void User::setUID(std::string uid)
{
    this->uid = uid;
}


