#include "player.h"

bool Player::isInGame() const
{
    return in_game;
}

void Player::setInGame(bool value)
{
    in_game = value;
}

bool Player::isGuarded() const
{
    return guarded;
}

void Player::setGuarded(bool value)
{
    guarded = value;
}

bool Player::isOnTurn() const
{
    return on_turn;
}

void Player::setOnTurn(bool value)
{
    on_turn = value;
}

long Player::getScore() const
{
    return score;
}

void Player::setScore(long value)
{
    score = value;
}

void Player::giveCard(GameCards card)
{
    myCard = card;
}

bool Player::compareCard(GameCards card)
{
    return (myCard == card);
}

GameCards Player::cardOnDesk()
{
    played_list.push_back(myCard);
    return myCard;
}

GameCards Player::showCard()
{
    return myCard;
}

void Player::setSecondCard(const GameCards &value)
{
    secondCard = value;
}

User *Player::getUser() const
{
    return user;
}

Player::Player(User *user) : user(user)
{
    
}
