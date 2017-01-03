#include "player.h"

#include "message/message.h"
#include "message/messagequeue.h"

bool Player::isGuarded() const
{
    return guarded;
}

void Player::setGuarded(bool value)
{
    guarded = value;
}


long Player::getScore() const
{
    return score;
}

void Player::setScore(long value)
{
    score = value;
}

void Player::giveFirstCard(GameCards card)
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
}

void Player::takeToken()
{
    token = false;
}

void Player::setSecondCard(const GameCards &value)
{
    secondCard = value;
    Message *msg = new Message(user->getSocket(),MessageType::game, Event::CAR, std::to_string(secondCard));
    MessageQueue::sendInstance()->push_msg(msg);
}

User *Player::getUser() const
{
    return user;
}

bool Player::isAlive() const
{
    return alive;
}

void Player::setAlive(bool value)
{
    alive = value;
}

GameCards Player::getSecondCard() const
{
    return secondCard;
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

    playerAtributes += "<token>";
    playerAtributes += token ? "true" : "false";
    playerAtributes += "</token>";

    playerAtributes += xmlCards();

    playerAtributes += "</player>";
    return playerAtributes;
}

bool Player::hasToken()
{
    return token;
}

void Player::sendCards()
{
    std::string msgS = "";
    msgS += std::to_string(myCard);
    msgS += "&&";
    msgS += std::to_string(secondCard);

    Message *msg = new Message(user->getSocket(),MessageType::game, Event::CAR, msgS);
    MessageQueue::sendInstance()->push_msg(msg);
}

Player::Player(User *user) : user(user)
  ,alive(true)
  ,myCard(GameCards::none)
  ,secondCard(GameCards::none)
{
}
