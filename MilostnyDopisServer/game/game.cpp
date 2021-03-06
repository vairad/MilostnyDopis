#include "game.h"

#include "log/log.h"

#include "message/messagequeue.h"
#include "users/userdatabase.h"

#define MAX_PLAYER_COUNT 4

std::string Game::getUid() const
{
    return uid;
}

Game::Game(std::string uid, int round_count, short maxPlayers) : uid(uid)
  , round_count(round_count)
  , playedRounds(0)
  , status_sequence_id(0)
  , player_count(0)
  , maxPlayerCount(maxPlayers)
  , full(false)
  , started(false)
  , player1(NULL)
  , player2(NULL)
  , player3(NULL)
  , player4(NULL)
{
    players[0] = &player1;
    players[1] = &player2;
    players[2] = &player3;
    players[3] = &player4;

    game_deck = new GameDeck();
}

Game::~Game(){
    if(player1 != NULL)
       delete player1;
    if(player2 != NULL)
        delete player2;
    if(player3 != NULL)
        delete player3;
    if(player4 != NULL)
        delete player4;

    delete game_deck;
}

//============================================================================================================================
//=================================================================================================
//=================================================================================================
//=================================================================================================
//=================================================================================================

/**
 * Urči hráče a jeho kartu, pokud zvolíš správně hráč je vyřazen ze hry.
 * Nelze zvolit strážnou a nelze zvolit hráče který je chtáněný komornou
 * @brief Game::effectGuardian
 * @param who
 * @param whom
 * @param tip
 * @return
 */
bool Game::effectGuardian(Player *who, Player *whom, GameCards tip, std::string *result)
{
    LOG_DEBUG("effectGuardian() - start");

    /* CHECK PREREQUISITES */
    if(whom->isGuarded()){
        LOG_TRACE("effectGuardian() - cíl je chráněný");
        *result = "GUARDED";
        return false;
    }
    if(tip == GameCards::guardian){
        LOG_TRACE("effectGuardian() - tip strazne proti pravidlům");
        *result = "WRONG";
        return false;
    }
    if(who == whom){
        LOG_TRACE("effectGuardian() - nelze určit sebe");
        *result = "SAME";
        return false;
    }

    /* CARD EFFECT */
    if(whom->compareCard(tip)){
        LOG_DEBUG("effectGuardian() - success");
        GameCards card = whom->cardOnDesk();
        this->sendCardToPlayers(card, whom);
        whom->setAlive(false);
        *result = "KILL";
    } else {
        LOG_DEBUG("effectGuardian() - miss");
        *result = "MISS";
    }
    return true;
}

/**
 * Podívej se na kartu zvoleného hráče, pokud není chráněn komornou.
 * @brief Game::effectPriest
 * @param who
 * @param whom
 * @return
 */
bool Game::effectPriest(Player *who, Player *whom, std::string *result)
{
    LOG_DEBUG("effectPriest() - start");

    /* CHECK PREREQUISITES */
    if(whom->isGuarded()){
        LOG_TRACE("effectPriest() - cíl je chráněný");
        *result = "GUARDED";
        return false;
    }
    if(who == whom){
        LOG_TRACE("effectPriest() - nelze určit sebe");
        *result = "SAME";
        return false;
    }
    /* DO EFFECT */
    LOG_TRACE("effectPriest() - effect");
    *result = std::to_string(whom->showCard());
    return true;
}

/**
 * Porovnej si karu se zvoleným hráčem, pokud není chráněn komornou.
 *
 * @brief Game::effectBaron
 * @param who
 * @param whom
 * @return
 */
bool Game::effectBaron(Player *who, Player *whom, std::string *result)
{
    LOG_DEBUG("effectBaron() - start");

    /* CHECK PREREQUISITES */
    if(whom->isGuarded()){
        LOG_TRACE("effectBaron() - cíl je chráněný");
        *result = "GUARDED";
        return false;
    }
    if(who == whom){
        LOG_TRACE("effectBaron() - nelze určit sebe");
        *result = "SAME";
        return false;
    }
    /* DO EFFECT */
    GameCards losersCard, winnerCard;
    Player *loser, *winner;

    who->effectCardSecond(GameCards::baron);

    if(whom->showCard() < who->showCard() && who->showCard() != GameCards::none){
        //win case
        losersCard = whom->cardOnDesk();
        loser = whom;
        winnerCard = who->showCard();
        winner = who;
        loser->setAlive(false);
        this->sendCardToPlayers(losersCard, loser);
    }else if( whom->showCard() == who->showCard() && who->showCard() != GameCards::none ){
        //draw case
        losersCard = whom->showCard();
        loser = whom;
        winnerCard = who->showCard();
        winner = who;
    }else{
        //lose case
        losersCard = who->cardOnDesk();
        loser = who;
        winnerCard = whom->showCard();
        winner = whom;
        loser->setAlive(false);
        this->sendCardToPlayers(losersCard, loser);
    }

    *result = "RESULT";
    *result += "@@";
    *result += std::to_string(losersCard);
    *result += "@@";
    *result += loser->getUser()->getUID();
    *result += "@@";
    *result += std::to_string(winnerCard);
    *result += "@@";
    *result += winner->getUser()->getUID();


    return true;
}

/**
 * Po celé další kolo jsi chráněn před efekty karet ostatních
 * @brief Game::effectMaid
 * @param who
 * @return
 */
bool Game::effectMaid(Player *who, std::string *result)
{
    LOG_DEBUG("effectMaid() - start");

    /* CHECK PREREQUISITES */

    /* DO EFFECT */
    who->setGuarded(true);
    *result = "SUCCESS";
    return true;
}

/**
 * Vylož kartu na stůl a pokud můžeš pokračovat vezmi si další.
 * @brief Game::effectPrince
 * @param who
 * @param whom
 * @return
 */
bool Game::effectPrince(Player *who, Player *whom, std::string *result)
{
    LOG_DEBUG("effectPrince() - start");

    /* CHECK PREREQUISITES */
    if(who->haveCountess()){
        LOG_TRACE("effectPrince() - musíš zahrát komornou");
        *result = "WRONG";
        return false;
    }
    if(whom->isGuarded()){
        LOG_TRACE("effectPrince() - cíl je chráněný");
        *result = "GUARDED";
        return false;
    }

    if(who == whom){
        who->effectCardSecond(GameCards::prince);
    }

    *result += std::to_string(whom->showCard());

    this->sendCardToPlayers(whom->cardOnDesk(), whom);
    if(whom->isAlive()){
        whom->giveFirstCard(game_deck->getNextCard());
    }

    return true;
}

/**
 * Vyměň si kartu s označeným hráčem, pokud není chráněn komornou.
 * @brief Game::effectKing
 * @param who
 * @param whom
 * @return
 */
bool Game::effectKing(Player *who, Player *whom, std::string *result)
{
    LOG_DEBUG("effectKing() - start");

    /* CHECK PREREQUISITES */
    if(who->haveCountess()){
        LOG_TRACE("effectKing() - musíš zahrát komornou");
        *result = "WRONG";
        return false;
    }
    if(whom->isGuarded()){
        LOG_TRACE("effectKing() - cíl je chráněný");
         *result = "GUARDED";
        return false;

    }
    if(who == whom){
        LOG_TRACE("effectKing() - nelze určit sebe");
        *result = "SAME";
        return false;
    }
    /* DO EFFECT */
    who->effectCardSecond(GameCards::king);
    GameCards tmpCard = whom->showCard();
    whom->giveFirstCard(who->showCard());
    who->giveFirstCard(tmpCard);
    return true;
}

/**
 * Vylož kartu, pokud se ti sejde na ruce hraběnka s králem nebo princem.
 * Nemá speciální efekt po odehrání.
 * @brief Game::effectCountess
 * @param who
 * @return
 */
bool Game::effectCountess(Player *who, std::string *result)
{
    LOG_DEBUG("effectCountess() - start");
    if(who->haveRoyalMan()){
        LOG_TRACE("effectCountess() - forced play");
    }
    *result = "SUCCESS";
    return true;
}


/**
 * Pokud zahraješ tuto kartu, hra pro tebe končí.
 * @brief Game::effectPrincess
 * @param who
 * @return
 */
bool Game::effectPrincess(Player *who, std::string *result)
{
    LOG_DEBUG("effectPrincess() - start");
    who->setAlive(false);
    *result = "SUCCESS";
    return true;
}

//=================================================================================================
//=================================================================================================
//=================================================================================================
//=================================================================================================
//=====================================================================================================================


/** *******************************************************************************************************************
 * Choose correct effect of cards
 *
 * @brief Game::playCard
 * @param result
 * @param cardToPlay
 * @param who
 * @param whom
 * @param tip
 * @return
 */
std::string Game::playCard(bool *result, GameCards cardToPlay, Player *who, Player *whom, GameCards cardTip)
{
    LOG_DEBUG("Game::playCard() - start switch")

    if(!who->haveThisCard(cardToPlay)){
        *result = false;
        return "ERROR You DO NOT have this card";
    }

    std::string resultS;
    switch(cardToPlay){
    case GameCards::guardian:
        LOG_TRACE("Game::playCard() - case guardian")
        *result = this->effectGuardian(who, whom, cardTip, &resultS);
        break;
    case GameCards::priest:
        LOG_TRACE("Game::playCard() - case priest")
        *result = this->effectPriest(who, whom, &resultS);
        break;
    case GameCards::baron:
        LOG_TRACE("Game::playCard() - case baron")
        *result = this->effectBaron(who, whom, &resultS);
        break;
    case GameCards::komorna:
        LOG_TRACE("Game::playCard() - case komorna")
        *result = this->effectMaid(who, &resultS);
        break;
    case GameCards::prince:
        LOG_TRACE("Game::playCard() - case prince")
        *result = this->effectPrince(who, whom, &resultS);
        break;
    case GameCards::king:
        LOG_TRACE("Game::playCard() - case king")
        *result = this->effectKing(who, whom, &resultS);
        break;
    case GameCards::countess:
        LOG_TRACE("Game::playCard() - case countess")
        *result = this->effectCountess(who, &resultS);
        break;
    case GameCards::princess:
        LOG_TRACE("Game::playCard() - case princess")
        *result = this->effectPrincess(who, &resultS);
        break;
    default:
        LOG_TRACE("Game::playCard() - case default")
        *result = false;
        break;
    }
    return resultS;
}


/**
 * @brief Game::addPlayer
 * @param who
 * @return
 */
bool Game::addPlayer(User *who)
{
    //check parameter
    if(who == NULL){
        return false;
    }

    // check if player is logged in this game
    if(player1 != NULL && player1->getUser() == who){
       return true;
    }
    if(player2 != NULL && player2->getUser() == who){
       return true;
    }
    if(player3 != NULL && player3->getUser() == who){
        return true;
     }
    if(player4 != NULL && player4->getUser() == who){
       return true;
    }

    // whenever game is started you cant join
    if(started == true){
        return false;
    }

    //login players
    if(player1 == NULL){
        player1 = new Player(who);
        player_count++;
        return true;
    }
    if(player2 == NULL){
        player2 = new Player(who);
        player_count++;
        return true;
    }
    if(player3 == NULL){
        player3 = new Player(who);
        player_count++;
        return true;
    }
    if(player4 == NULL){
        player4 = new Player(who);
        player_count++;
        return true;
    }
    return false;
}

/**
 * @brief Game::giveCard
 * @param who
 * @return
 */
bool Game::giveCard(Player *who)
{
    if(who->getSecondCard() == GameCards::none){
        who->setSecondCard(game_deck->getNextCard());
        return true;
    }
    return false;
}

/**
 * Přepne hru do stavu započaté
 * @brief Game::start
 */
void Game::start(){
    LOG_DEBUG("Game::start()");
    if(started == true){
        LOG_DEBUG("Game was already started");
        return;
    }
    started = true;
    for(int playerIndex = 0; playerIndex < getMaxPlayerCount(); playerIndex++){
        Player * player = getPlayer(playerIndex);
        if(player != NULL){
            player->giveFirstCard(game_deck->getNextCard());
        }
    }
    player1->giveToken();
    sendTokenTo(player1);
}

/**
 * @brief Game::moveTokenToNextPlayer
 * @param user
 */
void Game::moveTokenToNextPlayer(User *user)
{
    Player *playerWithToken = getPlayer(user);
    Player *playerToGetToken = getNextPlayerForToken(playerWithToken);
    if(playerToGetToken == NULL){
        return;
    }
    playerWithToken->takeToken();
    playerToGetToken->giveToken();
    sendTokenTo(playerToGetToken);
}

/**
 * @brief Game::getNextPlayerForToken
 * @param playerWithToken
 * @return
 */
Player *Game::getNextPlayerForToken(Player *playerWithToken){
    short indexWithToken = 0;
    for(; indexWithToken < MAX_PLAYER_COUNT; indexWithToken++){
        if(*players[indexWithToken] == playerWithToken){
            break;
        }
    }

    Player *chosenPlayer = NULL;
    int counter = 0;
    while(chosenPlayer == NULL){
        indexWithToken++;
        indexWithToken = indexWithToken % MAX_PLAYER_COUNT;
        Player *p = *players[indexWithToken];
        if(p != NULL && p->isAlive()){
            chosenPlayer = p;
        }
        counter ++;
        if(counter > 5){
            break;
        }
    }
    return chosenPlayer;
}

/** ***********************************************************************************************
 * Vrací hráče dle zadaného uživatele v této hře.
 * @brief Game::getPlayer
 * @param user ukazatel na uživatele svázaného s hráčem (může být null)
 * @return NULL / pointer na nalezeného hráče
 */
Player *Game::getPlayer(User *user){
   if(player1 != NULL && player1->getUser() == user){
       return player1;
   }
   if(player2 != NULL && player2->getUser() == user){
       return player2;
   }
   if(player3 != NULL && player3->getUser() == user){
       return player3;
   }
   if(player4 != NULL && player4->getUser() == user){
       return player4;
   }
   return NULL;
}

/**
 * @brief Game::getPlayer_count
 * @return
 */
short Game::getPlayer_count() const
{
    return player_count;
}

/**
 * @brief Game::getPlayer
 * @param index
 * @return
 */
Player *Game::getPlayer(int index){
    switch(index){
        case 0:
        return player1;
        case 1:
        return player2;
        case 2:
        return player3;
        case 3:
        return player4;
    }
    return NULL;
}

/**
 * @brief Game::isStarted
 * @return
 */
bool Game::isStarted()
{
    return started;
}


/**
 * @brief Game::toString
 * @return
 */
std::string Game::toString()
{
    std::string tmp;
    tmp += uid;
    tmp += "&&";
    tmp += std::to_string(player_count);
    tmp += "&&";
    tmp += std::to_string(started);
    return tmp;
}

/**
 * @brief Game::sendCardToPlayers
 * @param playedCard
 * @param player
 */
void Game::sendCardToPlayers(GameCards playedCard, Player *player)
{
    if(playedCard != GameCards::none ){
        std::string msgS = getUid();
        msgS += "&&";
        msgS += player->getUser()->getUID();
        msgS += "&&";
        msgS += std::to_string(playedCard);

        if(player1 != NULL){
            Message *msg = new Message(player1->getUser()->getSocket(),MessageType::game, Event::PLA, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
        if(player2 != NULL){
            Message *msg = new Message(player2->getUser()->getSocket(),MessageType::game, Event::PLA, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
        if(player3 != NULL){
            Message *msg = new Message(player3->getUser()->getSocket(),MessageType::game, Event::PLA, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
        if(player4 != NULL){
            Message *msg = new Message(player4->getUser()->getSocket(),MessageType::game, Event::PLA, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
    }
}

/**
 * @brief Game::sendCardsToPlayers
 * @param playedCard
 * @param player
 */
void Game::sendCardsToPlayers()
{
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            (*players[index])->sendCards();
        }
    }
}

/**
 * @brief Game::sendResult
 * @param who
 * @param whom
 * @param cardToPlay
 * @param resultS
 */
void Game::sendResult(Player *who, Player *whom, GameCards cardToPlay, std::string resultS)
{
    switch(cardToPlay){
    case guardian:
    case baron:
    case prince:
    case princess:
        if(resultS.compare("KILL")){
            this->sendPlayersState();
        }
        who->sendResult(cardToPlay, resultS, true);
        if(whom != NULL){
            whom->sendResult(cardToPlay, resultS, false);
        }
        break;
    case priest:
    case countess:
    case king:
    case komorna:
        who->sendResult(cardToPlay, resultS, true);
        if(whom != NULL){
            whom->sendResult(cardToPlay, resultS, false);
        }
        break;
    default:
        LOG_ERROR_P1("Nesmyslna karta k provedeni vysledku efektu :", cardToPlay);
        break;
    }
}

/**
 * Odešli stav hráčů na všechny klienty
 * @brief Game::sendPlayersState
 */
void Game::sendPlayersState(){
    std::string msgS = "";
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            msgS += (*players[index])->getStateMsg();
        }
    }

    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            Message *msg = new Message((*players[index])->getUser()->getSocket()
                                      ,MessageType::game
                                      ,Event::PLS
                                      , msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
    }
}

/**
 * @brief Game::isEndOfRound
 * @return
 */
bool Game::isEndOfRound(){
    if(game_deck->getGivedCount() > 14){
        return true;
    }

    short alivePlayersCount = 0;
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            if((*players[index])->isAlive()){
                alivePlayersCount ++ ;
            }
        }
    }
    if(alivePlayersCount == 1){
        return true;
    }
    return false;
}

/**
 * @brief Game::isEndOfGame
 * @return
 */
bool Game::isEndOfGame()
{
    if(playedRounds < round_count){
        return false;
    }
    return true;
}

/**
 * @brief Game::finishGame
 */
void Game::finishGame(){
    // říct kdo vyhrál kolo
  //  this->sendRoundResult();
    MSG_PS("Ukončuji hru", getUid().c_str());
    this->sendGameStateToAllPlayers();

    this->sendGoodBye();
    this->unlinkUsers();
}


/**
 * @brief Game::restartGame
 */
void Game::restartGame()
{
    playedRounds++;
    // říct kdo vyhrál kolo
    this->sendRoundResult();

    // resetovat stav
    this->resetState();
    this->sendGameStateToAllPlayers();
    // zvýšit čítač
}

int Game::getMaxPlayerCount()
{
    return maxPlayerCount;
}

bool Game::removePlayer(Player *player)
{
    if(started){
        return false;
    }
    if(player1 == player){
        player1 = NULL;
    }
    if(player2 == player){
        player2 = NULL;
    }
    if(player3 == player){
        player2 = NULL;
    }
    if(player4 == player){
        player3 = NULL;
    }
    player_count--;
    delete player;
    return true;
}


/**
 * @brief Game::sendTokenTo
 * @param player
 */
void Game::sendTokenTo(Player *player)
{
    if(player != NULL ){
        std::string msgS = getUid();
        msgS += "&&";
        msgS += player->getUser()->getUID();
        if(player1 != NULL){
            Message *msg = new Message(player1->getUser()->getSocket(),MessageType::game, Event::TOK, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
        if(player2 != NULL){
            Message *msg = new Message(player2->getUser()->getSocket(),MessageType::game, Event::TOK, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
        if(player3 != NULL){
            Message *msg = new Message(player3->getUser()->getSocket(),MessageType::game, Event::TOK, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
        if(player4 != NULL){
            Message *msg = new Message(player4->getUser()->getSocket(),MessageType::game, Event::TOK, msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
    }
}

/**
 * @brief Game::sendGoodBye
 */
void Game::sendGoodBye()
{
    std::string msgS = getUid();
    msgS += "&&";
    msgS += "OVER";

    if(player1 != NULL){
        Message *msg = new Message(player1->getUser()->getSocket(),MessageType::game, Event::STA, msgS);
        MessageQueue::sendInstance()->push_msg(msg);
    }
    if(player2 != NULL){
        Message *msg = new Message(player2->getUser()->getSocket(),MessageType::game, Event::STA, msgS);
        MessageQueue::sendInstance()->push_msg(msg);
    }
    if(player3 != NULL){
        Message *msg = new Message(player3->getUser()->getSocket(),MessageType::game, Event::STA, msgS);
        MessageQueue::sendInstance()->push_msg(msg);
    }
    if(player4 != NULL){
        Message *msg = new Message(player4->getUser()->getSocket(),MessageType::game, Event::STA, msgS);
        MessageQueue::sendInstance()->push_msg(msg);
    }
}

/**
 * @brief Game::sendRoundResult
 */
void Game::sendRoundResult(){
    GameCards winnerCard = GameCards::none;

    // find best card
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL && (*players[index])->isAlive()){
            if(winnerCard == GameCards::none){
               winnerCard = (*players[index])->showCard();
               continue;
            }
            if((*players[index])->showCard() > winnerCard){
                winnerCard = (*players[index])->showCard();
                continue;
            }
        }
    }

    //find all winners
    std::string msgS = "";
    msgS += std::to_string(winnerCard);
    msgS += "&&";
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL
                && (*players[index])->isAlive()
                && (*players[index])->showCard() == winnerCard){
            (*players[index])->givePoint();
            msgS += (*players[index])->getUser()->getUID();
            msgS += "&&";
        }
    }

    //send results
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            Message *msg = new Message((*players[index])->getUser()->getSocket()
                                           ,MessageType::game
                                           ,Event::PTS
                                           ,msgS);
            MessageQueue::sendInstance()->push_msg(msg);
        }
    }
}

/**
 * @brief Game::getRoundPoints
 * @return
 */
std::string Game::getRoundPoints()
{
    std::string pointsS = "";
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            pointsS += (*players[index])->getPointsMessage();
            pointsS += "&&";
        }
    }
    return pointsS;
}

/**
 * @brief Game::unlinkUsers
 */
void Game::unlinkUsers()
{
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            (*players[index])->getUser()->setGame(NULL);
            if((*players[index])->getUser()->isToDelete()){
                UserDatabase::getInstance()
                        ->removeUser((*players[index])->getUser()->getUID());
            }
        }
    }
}

/**
 * Nastaví všechny stavové proměnné tykající se kola do výchozího stavu.
 * @brief Game::resetState
 */
void Game::resetState()
{
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            (*players[index])->clear();
        }
    }
    game_deck = new GameDeck();

    for(int playerIndex = 0; playerIndex < getMaxPlayerCount(); playerIndex++){
        Player *player = getPlayer(playerIndex);
        if(player != NULL){
            player->giveFirstCard(game_deck->getNextCard());
        }
    }
}

/**
 * @brief Game::sendGameStateToAllPlayers
 */
void Game::sendGameStateToAllPlayers()
{
    std::string gameStatus = this->getStatus();
    for (int index = 0; index < MAX_PLAYER_COUNT; ++index) {
        if((*players[index]) != NULL){
            Message *msg = new Message((*players[index])->getUser()->getSocket()
                                       ,MessageType::game, Event::STA
                                       ,gameStatus);
            MessageQueue::sendInstance()->push_msg(msg);
        }
    }
}

//==============
//==============
//==============
//=========================================== CREATE XML OF GAME STATUS ============
//==============
//==============
//==============

/** *************************************************************************
 * Vrací aktuální stav hry pro možné zařazení uživatele do hry po výpadku atp
 * @brief Game::getStatus
 * @return
 */
std::string Game::getStatus()
{
    std::string msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  //  msg += "<gameStatus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.w3schools.com\" xsi:schemaLocation=\"http://home.zcu.cz/~vaisr test.xsd\" >";
    msg += "<gameStatus>";

    msg += xmlGameId();
    msg += xmlGameSeq();

    msg += xmlPlayerCollection();

    msg += xmlGameRound();
    msg += xmlGameRoundCount();

    msg += "</gameStatus>";
    return msg;
}

std::string Game::xmlGameId()
{
    std::string gameId = "";

    gameId += "<id>";
    gameId += this->getUid();
    gameId += "</id>";

    return gameId;
}

std::string Game::xmlGameRound()
{
    std::string gameRound = "";

    gameRound += "<round>";
    gameRound += std::to_string(playedRounds + 1);
    gameRound += "</round>";

    return gameRound;
}

std::string Game::xmlGameRoundCount()
{
    std::string gameRound = "";

    gameRound += "<roundCount>";
    gameRound += std::to_string(round_count);
    gameRound += "</roundCount>";

    return gameRound;
}

std::string Game::xmlGameSeq()
{
    std::string gameSeq = "";

    status_sequence_id++;

    gameSeq += "<seq>";
    gameSeq += std::to_string(status_sequence_id);
    gameSeq += "</seq>";

    return gameSeq;
}



std::string Game::xmlPlayerCollection()
{
    std::string playerCollection = "<playersCollection>";

    if(player1 != NULL)
        playerCollection += player1->xmlPlayer(1);
    if(player2 != NULL)
        playerCollection += player2->xmlPlayer(2);
    if(player3 != NULL)
        playerCollection += player3->xmlPlayer(3);
    if(player4 != NULL)
        playerCollection += player4->xmlPlayer(4);

    playerCollection += "</playersCollection>";
    return playerCollection;
}
