#include "player.h"

#include "message/message.h"
#include "message/messagequeue.h"

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
    Message *msg = new Message(user->getSocket(),MessageType::game, Event::CAR, std::to_string(card));
    MessageQueue::sendInstance()->push_msg(msg);
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

void Player::giveToken()
{
    token = true;
    Message *msg = new Message(user->getSocket(),MessageType::game, Event::CAR, "token");
    MessageQueue::sendInstance()->push_msg(msg);
}

void Player::setSecondCard(const GameCards &value)
{
    secondCard = value;
}

User *Player::getUser() const
{
    return user;
}

bool Player::getIs_alive() const
{
    return alive;
}

void Player::setIs_alive(bool value)
{
    alive = value;
}

std::string Player::xmlCards()
{
    std::string cardsCollection = "<cardsCollection>";

    for(auto card = played_list.begin(); card != played_list.end(); ++card) {
        cardsCollection += GameDeck::cardToXml(*card);
        card++;
    }

    cardsCollection += "</cardsCollection>";
    return cardsCollection;
}


std::string Player::xmlPlayer(int order)
{
    if(user == NULL){
        return "";
    }
    std::string playerAtributes = "<player>";

    playerAtributes += "<order>";
    playerAtributes += std::to_string(order);
    playerAtributes += "</order>";

    playerAtributes += "<name>";
    playerAtributes += *getUser()->getNickname();
    playerAtributes += "</name>";

    playerAtributes += "<id>";
    playerAtributes += getUser()->getUID();
    playerAtributes += "</id>";

    playerAtributes += "<alive>";
    playerAtributes += alive ? "true" : "false";
    playerAtributes += "</alive>";

    playerAtributes += xmlCards();

    playerAtributes += "</player>";
    return playerAtributes;
}

Player::Player(User *user) : user(user)
{
    
}
