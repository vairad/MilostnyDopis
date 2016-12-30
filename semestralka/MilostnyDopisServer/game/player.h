#ifndef PLAYER_H
#define PLAYER_H

#include "users/user.h"
#include "game/package.h"
#include <vector>

class User;

class Player
{
    User *user;

    bool in_game;
    bool guarded;
    bool on_turn;

    bool token;

    long score;

    GameCards myCard;
    GameCards secondCard;

    std::vector<GameCards> played_list;

public:
    Player(User *user);

    bool isInGame() const;
    void setInGame(bool value);
    bool isGuarded() const;
    void setGuarded(bool value);
    bool isOnTurn() const;
    void setOnTurn(bool value);

    long getScore() const;
    void setScore(long value);

    void giveCard(GameCards card);
    void setSecondCard(const GameCards &value);

    bool compareCard(GameCards card);
    GameCards cardOnDesk();
    GameCards showCard();

    void giveToken();

    User *getUser() const;
};

#endif // PLAYER_H
