#include "gameservices.h"

#include "log/log.h"
#include "errornumber.h"

//==================================================================================
//=========================
//=======================       INICIALIZACNI BLOK SINGLETON
//=====================

GameServices *GameServices::INSTANCE = new GameServices();

GameServices *GameServices::getInst()
{
    return INSTANCE;
}

GameServices::GameServices()
{
    LOG_INFO("GameServices::GameServices()");
    int result = pthread_mutex_init(&map_lock, NULL);    // inicializuj zámek
    if(result != 0){
        //todo handle errors
        LOG_ERROR("NEOŠETŘENÁ CHYBA PŘI VYTVÁŘENÍ MUTEXU");
        MSG("Chyba při inicializaci... Ukončuji program");
        exit(MUTEX_NOT_INIT_GAMS);
    }
    LOG_TRACE("mutex inicializovan");
}

GameServices::~GameServices(){
    pthread_mutex_destroy(&map_lock);
}
//=====================
//=======================
//=========================
//==================================================================================

/** ************************************************************************************
 * @brief GameServices::createNewGame
 * @param round_count
 * @return
 */
Game *GameServices::createNewGame(int round_count)
{
    pthread_mutex_lock(&map_lock);
    std::string uid = getNextID();
    Game *game = new Game(uid, round_count);
    games_by_id[uid] = game;
    pthread_mutex_unlock(&map_lock);
    return game;
}

/** **************************************************************************************
 * @brief GameServices::getGameByUid
 * @param uid
 * @return
 */
Game *GameServices::getGameByUid(std::string *uid)
{
    if (games_by_id.find(*uid) == games_by_id.end()){
        return NULL;
    }
    return games_by_id[*uid];
}

/** **************************************************************************************
 * @brief GameServices::existGameByUid
 * @param uid
 * @return
 */
bool GameServices::existGameByUid(std::string *uid)
{
    if(getGameByUid(uid) == NULL){
        return false;
    }
    return true;
}

/** **********************************************************************************
 * Metoda nalezne další volné ID, které není použito v mapě vytvořených her.
 * Ke geneování používá metodu getRandomUID();
 * @brief GameServices::getNextID
 * @return random UID into games map
 */
std::string GameServices::getNextID(){
    std::string id = generateGameUID();
    while (games_by_id.find(id) != games_by_id.end()){
        id = generateGameUID();
    }
    return id;
}

//=====================
//=======================
//=========================
//================================================================================
//=========================
//=======================       STATICKY BLOK
//=====================

/** **********************************************************************************
 * Metoda vytvoří z alfanumerických znaků (GAMEad...) Dle nastavení definice UID_LEN
 * @brief GameServices::generateGameUID()
 * @return alphanumeric ID ... length = UID_LEN + 4
 */
std::string GameServices::generateGameUID()
{
    std::string key = "";
    static const char alphanum[] =
        "abcdefghijklmnopqrstuvwxyz";

        key += "GAME";
    for (int i = 0; i < UID_LEN; ++i) {
        key += alphanum[rand() % (sizeof(alphanum) - 1)];
    }
    return key;
}
