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
    GameCards tmpCard = myCard;
    myCard = GameCards::none;
    played_list.push_back(tmpCard);
    if(tmpCard == GameCards::princess){
        alive = false;
    }
    return tmpCard;
}

GameCards Player::showCard()
{
    return myCard;
}

void Player::giveToken()
{
    guarded = false;
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

void Player::playCard(GameCards card)
{
    if(myCard == card){
        myCard = secondCard;
        secondCard = GameCards::none;
    }else if(secondCard == card){
        secondCard = GameCards::none;
    }
    played_list.push_back(card);
}

bool Player::haveCountess()
{
    if(myCard == GameCards::countess || secondCard == GameCards::countess){
        return true;
    }
    return false;
}

bool Player::haveRoyalMan()
{
    if(myCard == GameCards::king || secondCard == GameCards::king){
        return true;
    }
    if(myCard == GameCards::prince || secondCard == GameCards::prince){
        return true;
    }
    return false;
}

void Player::sendResult(GameCards cardToPlay, std::string result, bool myCard)
{
   std::string msgS = std::to_string(cardToPlay);
   msgS += "&&";
   msgS += this->getUser()->getUID();
   msgS += "&&";
   msgS += myCard ? "true" : "false";
   msgS += "&&";
   msgS += result;

   Message *msg = new Message(this->getUser()->getSocket()
                              , MessageType::game
                              , Event::RES
                              , msgS);
    MessageQueue::sendInstance()->push_msg(msg);
}

bool Player::haveThisCard(GameCards card)
{
    if(myCard == card){
        return true;
    }
    if(secondCard == card){
        return true;
    }
    return false;
}

std::string Player::getStateMsg()
{
    std::string msg;
    msg += user->getUID();
    msg += "&&";
    msg += guarded ? "true" : "false";
    msg += "&&";
    msg += token ? "true" : "false";
    msg += "&&";
    msg += alive ? "true" : "false";
    msg += "@@";
    return msg;
}

std::string Player::getPointsMessage(){
    std::string msg = "";
    msg += getUser()->getUID();
    msg += "@@";
    msg += std::to_string(this->getPoints());
    return msg;
}

void Player::clear()
{
    myCard = GameCards::none;
    secondCard = GameCards::none;
    played_list.clear();
    alive = true;
    guarded = false;
}

int Player::getPoints(){
    int points = 0;
    for(auto card = played_list.begin(); card != played_list.end(); ++card) {
        points += *card;
    }
    if(myCard != GameCards::none){
        points += myCard;
    }
    return points;
}

std::string Player::xmlCards()
{
    std::string cardsCollection = "<cardsCollection>";

    for(auto card = played_list.begin(); card != played_list.end(); ++card) {
        cardsCollection += GameDeck::cardToXml(*card);
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

    playerAtributes += "<guarded>";
    playerAtributes += guarded ? "true" : "false";
    playerAtributes += "</guarded>";

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

Player::Player(User *user) :
   user(user)
  ,guarded(false)
  ,alive(true)
  ,token(false)
  ,score(0)
  ,myCard(GameCards::none)
  ,secondCard(GameCards::none)
{
}

Player::~Player(){
    user->setGame(NULL);
}
