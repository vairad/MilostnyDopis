#ifndef USERDATABASE_H
#define USERDATABASE_H

/** ****************************************************************************
 *
 * Definice třídy UserDatabase, která slouží k ukldání infomací o uživatelích
 * přihlášených na serveru.
 *
 * Poskytuje funkcionalitu spojenou s jejich vyhledáváním, zařazováním a výpisem.
 *
 *******************************************************************************/

#include <map>

#include "users/user.h"

#define UID_LEN 5

class UserDatabase
{
    static UserDatabase *INSTANCE;
    std::map<std::string, User *> users;
public:
    static UserDatabase *getInstance();
    std::string addUser(User *user);
private:
    UserDatabase();
    std::string getNextID();
    std::string genRandomUid();
};

#endif // USERDATABASE_H
