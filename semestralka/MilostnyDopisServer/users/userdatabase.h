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

using namespace std;

class UserDatabase
{
    static UserDatabase *INSTANCE;

    pthread_mutex_t map_lock;
    map<string, User *> users_by_id;
    map<int, string> keys_by_socket;
public:
    static UserDatabase *getInstance();
    string addUser(User *user, int socket);
    User *getUserById(string key);
    User *getUserBySocket(int socket);
    bool hasSocketUser(int socket);
    bool existUserID(string key);
    void removeSocketUser(int socket);
    void setSocketUser(string key, int socket);
    map<string, User *>::iterator begin();
    map<string, User *>::iterator end();
private:
    UserDatabase();
    string getNextID();
    static inline string genRandomUid();
};

#endif // USERDATABASE_H
