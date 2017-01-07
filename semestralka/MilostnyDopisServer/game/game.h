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

    int round_count;
    int playedRounds;
    unsigned long status_sequence_id;
    short player_count;

    bool full;
    bool started;

    Player *player1 = NULL;
    Player *player2 = NULL;
    Player *player3 = NULL;
    Player *player4 = NULL;

    Player **players[4];

    GameDeck *game_deck;

public:
    Game(std::string uid, int round_count = 5);
    ~Game();

    bool addPlayer(User *who);

    /* card effects methods - check if turn is possible */
    bool effectGuardian(Player *who, Player *whom, GameCards tip, std::string *result);
    bool effectPriest(  Player *who, Player *whom, std::string *result);
    bool effectBaron(   Player *who, Player *whom, std::string *result);
    bool effectMaid(    Player *who,               std::string *result);
    bool effectPrince(  Player *who, Player *whom, std::string *result);
    bool effectKing(    Player *who, Player *whom, std::string *result);
    bool effectCountess(Player *who,               std::string *result);
    bool effectPrincess(Player *who,               std::string *result);
    /* card effects methods  end*/

    /* playCard play choose the ccorrect effect ... checking possibility of turn */
    std::string playCard(bool *result, GameCards cardToPlay, Player *who, Player *whom, GameCards cardTip);

    bool giveCard(Player *who);

    /* change state of game to started */
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

    void sendCardToPlayers(GameCards playedCard, Player *player);
    void sendCardsToPlayers();
    void sendResult(Player *who, Player *whom, GameCards cardToPlay, std::string resultS);
    void sendPlayersState(GameCards cardToPlay, Player *who, Player *whom);

    bool isEndOfRound();
    bool isEndOfGame();

    void finishGame();
    void restartGame();
private:
    void sendTokenTo(Player *player);
    void sendGoodBye();
    void sendRoundResult();

   std::string getRoundPoints();

    void unlinkUsers();

    void resetState();
    void sendGameStateToAllPlayers();

    std::string xmlPlayerCollection();
    std::string xmlGameId();
    std::string xmlGameSeq();
};

#endif // GAME_H
