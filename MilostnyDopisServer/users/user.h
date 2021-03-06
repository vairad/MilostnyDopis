#ifndef USER_H
#define USER_H

#include <string>

#include "game/gameservices.h"
#include "game/game.h"

class Game;
class User
{
    std::string *nickname;
    int socket;
    std::string uid;
    Game *game;
    bool toDelete;
public:
    User(std::string *nickname, int socket);
    void setUID(std::string uid);
    std::string getUID() const;
    int getSocket() const;
    std::string *getNickname() const;
    void setSocket(int value);
    std::string toString();
    std::string toNet();
    Game *getGame() const;
    void setGame(Game *value);
    void setToDelete();
    bool isToDelete();
};

#endif // USER_H
