#include "user.h"

User::User(std::string *nickname, int socket) :
                                        nickname(nickname)
                                      , socket(socket)
{

}

void User::setUID(std::string uid)
{
    this->uid = uid;
}
