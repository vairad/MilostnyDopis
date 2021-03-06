#ifndef PLAYER_H
#define PLAYER_H

#include "users/user.h"
#include "game/package.h"
#include <vector>

class User;

class Player
{
    User *user;

    bool guarded;

    bool alive;

    bool token;

    long score;

    GameCards myCard;
    GameCards secondCard;

    int points;

    std::vector<GameCards> played_list;
    std::string xmlCards();
public:
    Player(User *user);
    ~Player();

    bool isGuarded() const;
    void setGuarded(bool value);

    long getScore() const;
    void setScore(long value);

    void giveFirstCard(GameCards card);
    void setSecondCard(const GameCards &value);

    bool compareCard(GameCards card);
    GameCards cardOnDesk();
    GameCards showCard();

    void giveToken();
    void takeToken();

    User *getUser() const;
    bool isAlive() const;
    void setAlive(bool value);
    std::string xmlPlayer(int order);
    bool hasToken();
    void sendCards();
    GameCards getSecondCard() const;
    void playCard(GameCards card);
    bool haveCountess();
    bool haveRoyalMan();
    void sendResult(GameCards cardToPlay, std::string result, bool myCard);
    bool haveThisCard(GameCards card);
    std::string getStateMsg();
    std::string getPointsMessage();
    void clear();
    int getPoints();
    void givePoint();
    void effectCardSecond(GameCards card);
    void removeUser();
};

#endif // PLAYER_H
