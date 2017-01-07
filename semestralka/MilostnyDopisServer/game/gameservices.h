#ifndef GAMESERVICES_H
#define GAMESERVICES_H

#include "game/game.h"

#include <map>
#include <pthread.h>

class Game;
class GameServices
{
    static const short GAME_UID_LEN = 2;

    GameServices();
    static GameServices *INSTANCE;

    pthread_mutex_t map_lock;
    std::map<std::string, Game *> games_by_id;

public:
    static GameServices *getInst();

    Game *createNewGame(int round_count);
    Game *getGameByUid(std::string uid);
    bool existGameByUid(std::string uid);

    void removeGame(Game *game);

    std::string listGames();

    ~GameServices();

private:
    static std::string generateGameUID();
    std::string getNextID();
};

#endif // GAMESERVICES_H
