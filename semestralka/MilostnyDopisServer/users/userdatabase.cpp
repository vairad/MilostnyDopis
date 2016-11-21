#include "userdatabase.h"

#include "log/log.h"
#include "errornumber.h"

//==========================INIT SINGLETON=============================================

UserDatabase *UserDatabase::INSTANCE = new UserDatabase();

UserDatabase::UserDatabase()
{
    LOG_INFO("UserDatabase::UserDatabase()");
    int result = pthread_mutex_init(&map_lock, NULL);    // inicializuj zámek
    if(result != 0){
        //todo handle errors
        LOG_ERROR("NEOŠETŘENÁ CHYBA PŘI VYTVÁŘENÍ MUTEXU");
        MSG("Chyba při inicializaci... Ukončuji program");
        exit(MUTEX_NOT_INIT_USRS);
    }
    LOG_TRACE("mutex inicializovan");

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
std::string UserDatabase::addUser(User *user, int socket)
{
    pthread_mutex_lock(&map_lock);
    std::string id = getNextID();
    user->setUID(id);
    users_by_id[id] = user;
    setSocketUser(id, socket);
    pthread_mutex_unlock(&map_lock);
    return id;
}

/** **********************************************************************************
 * Vrátí ukazatel na uživatele, dle zadaného klíče
 * @brief UserDatabase::getUserById
 * @param key
 * @return
 */
User *UserDatabase::getUserById(std::string key)
{
    if (users_by_id.find(key) == users_by_id.end()){
        return NULL;
    }
    return users_by_id[key];
}

/** **********************************************************************************
 * @brief UserDatabase::getUserBySocket
 * @param socket
 * @return
 */
User *UserDatabase::getUserBySocket(int socket)
{
    if (keys_by_socket.find(socket) == keys_by_socket.end()){
       return NULL;
    }
    return getUserById(keys_by_socket[socket]);
}

/** **********************************************************************************
 * Odstraní vazbu soket uživatel po jeho odpojení
 * @brief UserDatabase::removeSocketUser
 * @param socket
 * @return
 */
void UserDatabase::removeSocketUser(int socket)
{
    keys_by_socket.erase(socket);
}

/** ***********************************************************************************
 * @brief UserDatabase::setSocketUser
 * @param key
 * @param socket
 */
void UserDatabase::setSocketUser(string key, int socket)
{
    keys_by_socket[socket] = key;
}

map<string, User *>::iterator UserDatabase::begin()
{
    return users_by_id.begin();
}

map<string, User *>::iterator UserDatabase::end()
{
    return users_by_id.end();
}

/** *********************************************************************************
 * @brief UserDatabase::hasSocketUser
 * @param socket
 * @return
 */
bool UserDatabase::hasSocketUser(int socket)
{
    if(getUserBySocket(socket) == NULL){
        return false;
    }
    return true;
}

/** *****************************************************************************
 * @brief UserDatabase::existUserID
 * @param key
 * @return
 */
bool UserDatabase::existUserID(string key)
{
    if(getUserById(key) == NULL){
        return false;
    }
    return true;
}

/** **********************************************************************************
 * Metoda nalezne další volné ID, které není použito v mapě uživatelů.
 * Ke geneování používá metodu getRandomUID();
 * @brief UserDatabase::getNextID
 * @return random UID into users map
 */
std::string UserDatabase::getNextID(){
    std::string id = genRandomUid();
    while (users_by_id.find(id) != users_by_id.end()){
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


