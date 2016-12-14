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

    short player_count = 0;

    int round_count;

    bool full = false;
    bool started = false;


    GameDeck game_deck;

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
    std::string toString();
    std::string getUid() const;
};

#endif // GAME_H
