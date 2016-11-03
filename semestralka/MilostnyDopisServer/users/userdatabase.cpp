#include "userdatabase.h"
/** ****************************************************************************
 * UserDatabase class implementation.
 *
 */

//==========================INIT SINGLETON=============================================

UserDatabase *UserDatabase::INSTANCE = new UserDatabase();

UserDatabase::UserDatabase()
{

}

UserDatabase *UserDatabase::getInstance()
{
    return INSTANCE;
}

//=======================================================================================

/** **********************************************************************************
 * Metoda nalezne nové ID uživatele a vloží ho do kolekce
 * @brief UserDatabase::addUser
 * @param user User to add into collection
 * @return uid of added user
 */
std::string UserDatabase::addUser(User *user)
{
    std::string id = getNextID();
    user->setUID(id);
    users[id] = user;
    return id;
}

/** **********************************************************************************
 * Metoda nalezne další volné ID, které není použito v mapě uživatelů.
 * Ke geneování používá metodu getRandomUID();
 * @brief UserDatabase::getNextID
 * @return random UID into users map
 */
std::string UserDatabase::getNextID(){
    std::string id = genRandomUid();
    while (users.find(id) != users.end()){
        id = genRandomUid();
    }
    return id;
}

/** **********************************************************************************
 * Metoda vytvoří z alfanumerických znaků (aA9...) Dle nastavení definice UID_LEN
 * @brief UserDatabase::genRandomUid
 * @return alphanumeric ID ... length = UID_LEN
 */
std::string UserDatabase::genRandomUid() {
    std::string key = "";
    static const char alphanum[] =
        "0123456789"
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "abcdefghijklmnopqrstuvwxyz";

    for (int i = 0; i < UID_LEN; ++i) {
        key += alphanum[rand() % (sizeof(alphanum) - 1)];
    }

    return key;
}


