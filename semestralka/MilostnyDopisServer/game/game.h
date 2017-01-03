#ifndef GAME_H
#define GAME_H

#include "users/user.h"
#include "game/package.h"
#include "game/player.h"

class Player;
class User;
/**
 * Třída, která má na starost obsluhu všech herních událostí a interakcí s hráči.
 * @brief The Game class
 */
class Game
{
    std::string uid;

    Player *player1 = NULL;
    Player *player2 = NULL;
    Player *player3 = NULL;
    Player *player4 = NULL;

    Player **players[4];

    short player_count;

    int round_count;

    bool full = false;
    bool started = false;

    unsigned long status_sequence_id;

    GameDeck game_deck;

    std::string xmlPlayerCollection();
    std::string xmlGameId();

public:
    Game(std::string uid, int round_count = 5);

    bool addPlayer(User *who);

    bool effectGuardian(Player *who, Player *whom, GameCards tip);
    GameCards effectPriest(Player *who, Player *whom);
    bool effectBaron(Player *who, Player *whom);
    bool effectMaid(Player *who);
    bool effectPrince(Player *who, Player *whom);
    bool effectKing(Player *who, Player *whom);
    bool effectCountess(Player *who);
    bool effectPrincess(Player *who);
    bool giveCard(Player *who);

    void start();

    void moveTokenToNextPlayer(User *user);
    Player *getNextPlayerForToken(Player *playerWithToken);

    std::string getStatus();
    std::string toString();
    std::string getUid() const;
    short getPlayer_count() const;
    Player *getPlayer(int index);
    Player *getPlayer(User *user);

    bool isStarted();
private:
    std::string xmlGameSeq();
    void sendTokenTo(Player *player);

};

#endif // GAME_H
