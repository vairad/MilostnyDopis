#include "game.h"

#include "log/log.h"

#include "message/messagequeue.h"

std::string Game::getUid() const
{
    return uid;
}

Game::Game(std::string uid, int round_count) : uid(uid)
  , round_count(round_count)
  , status_sequence_id(0)
  , player_count(0)
  , player1(NULL)
  , player2(NULL)
  , player3(NULL)
  , player4(NULL)
{
    players[0] = &player1;
    players[1] = &player2;
    players[2] = &player3;
    players[3] = &player4;
}

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
bool Game::effectGuardian(Player *who, Player *whom, GameCards tip)
{
    if(whom->isGuarded()){
        return false;
    }
    if(whom->compareCard(tip)){
        whom->cardOnDesk();
        whom->setAlive(false);
        return true;
    }
    return false;
}

/**
 * Podívej se na kartu zvoleného hráče, pokud není chráněn komornou.
 * @brief Game::effectPriest
 * @param who
 * @param whom
 * @return
 */
GameCards Game::effectPriest(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return GameCards::none;
    }
    return whom->showCard();
}

/**
 * Porovnej si karu se zvoleným hráčem, pokud není chráněn komornou.
 *
 * @brief Game::effectBaron
 * @param who
 * @param whom
 * @return
 */
bool Game::effectBaron(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return false;
    }
    return whom->compareCard(who->showCard());
}

/**
 * Po celé další kolo jsi chráněn před efekty karet ostatních
 * @brief Game::effectMaid
 * @param who
 * @return
 */
bool Game::effectMaid(Player *who)
{
    who->setGuarded(true);
    return true;
}

/**
 * Vylož kartu na stůl a pokud můžeš pokračovat vezmi si další.
 * @brief Game::effectPrince
 * @param who
 * @param whom
 * @return
 */
bool Game::effectPrince(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return false;
    }
    whom->cardOnDesk();
    if(whom->isAlive()){
        giveCard(whom);
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
bool Game::effectKing(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return false;
    }
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
bool Game::effectCountess(Player *who)
{
    // no special efect when card is played
}


/**
 * Pokud zahraješ tuto kartu, hra pro tebe končí.
 * @brief Game::effectPrincess
 * @param who
 * @return
 */
bool Game::effectPrincess(Player *who)
{
    who->setAlive(false);
    return true;
}


//=================================================================================================
//=================================================================================================
//=================================================================================================
//=================================================================================================
//=====================================================================================================================

/**
 * @brief Game::giveCard
 * @param who
 * @return
 */
bool Game::giveCard(Player *who)
{
    who->setSecondCard(game_deck.getNextCard());
    return true;
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
    for(int playerIndex = 0; playerIndex < player_count; playerIndex++){
        getPlayer(playerIndex)->giveFirstCard(game_deck.getNextCard());
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
    for(; indexWithToken < 4; indexWithToken++){
        if(*players[indexWithToken] == playerWithToken){
            break;
        }
    }

    Player *chosenPlayer = NULL;
    while(chosenPlayer == NULL){
        indexWithToken++;
        indexWithToken = indexWithToken % 4;
        Player *p = *players[indexWithToken];
        if(p != NULL && p->isAlive()){
            chosenPlayer = p;
        }

    }
    return chosenPlayer;
}

/**
 * @brief Game::getPlayer
 * @param user
 * @return
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
