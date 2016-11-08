#ifndef PLAYER_H
#define PLAYER_H

#include "users/user.h"

class Player
{
    User *user;

    bool in_game;
    bool guarded;
    bool on_turn;

    long score;

public:
    Player( User *user);
};

#endif // PLAYER_H
