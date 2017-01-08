#include "gamehandler.h"

#include "errornumber.h"
#include "log/log.h"

#include "message/messagequeue.h"

#include "users/userdatabase.h"
#include "game/gameservices.h"

#include "util/utilities.h"

#include <cstring>

/**
 * @brief GameHandler::handleTypeGame
 * @param msg
 */
void GameHandler::handleTypeGame(Message *msg)
{
   Event type = msg->getEvent();
    switch (type){
    case Event::ECH : //echo event
        handleGameECH(msg);
        break;
    case Event::COD : //echo event
        handleGameCOD(msg);
        break;
    case Event::NEW:
        handleGameNEW(msg);
        break;
    case Event::STA:
        handleGameSTA(msg);
        break;
    case Event::TOK:
        handleGameTOK(msg);
        break;
    case Event::CAR:
        handleGameCAR(msg);
        break;
    case Event::PLA:
        handleGamePLA(msg);
        break;
    case Event::UNK :
    default:
        //todo impl

        break;
    }
}

// ============================================================================================
/**
 * @brief GameHandler::checkLogged
 * @param socket
 * @return
 */
bool GameHandler::checkLogged(int socket){
    LOG_DEBUG("GameHandler::checkLogged() - start");
    if(!UserDatabase::getInstance()->hasSocketUser(socket)){
        MSG("Nepřihlášený uživatel se pokouší provést operaci");
        LOG_TRACE("Nepřihlášený uživatel se pokouší provést operaci");
        return false;
    }
    return true;
}

/**
 * @brief GameHandler::checkGame
 * @param uid
 * @return
 */
bool GameHandler::checkGame(std::string uid){
    LOG_DEBUG("GameHandler::checkGame() - start");
    if(!GameServices::getInst()->existGameByUid(uid)){
        MSG("Neexistujici ID Hry, nelze připojit uživatele");
        LOG_DEBUG_PS("Neexistujici ID Hry", uid.c_str());
        return false;
    }
    return true;
}

// ===================================================================================================
/**
 * @brief GameHandler::handleGameECH
 * @param msg
 */
void GameHandler::handleGameECH(Message *msg){
    LOG_DEBUG("GameHandler::handleGameECH() - start");
    msg->setMsg(GameServices::getInst()->listGames());
    msg->setEvent(Event::ACK);
    MessageQueue::sendInstance()->push_msg(msg);
}

/**
 * Oddešle stav celé hry na klienta
 * @brief GameHandler::handleGameSTA
 * @param msg
 */
void GameHandler::handleGameSTA(Message *msg){
    LOG_DEBUG("GameHandler::handleGameSTA() - start");
    if(!checkLogged(msg->getSocket())){
        //todo negativní odpověď
        return;
    }

    if(!checkGame(msg->getMsg())){
        //todo negativní odpověď
        return;
    }


    std::string gameStatus = GameServices::getInst()->getGameByUid(msg->getMsg())->getStatus();

    msg->setMsg(gameStatus);
    MessageQueue::sendInstance()->push_msg(msg);
}

/**
 * @brief GameHandler::handleGameTOK
 * @param msg
 */
void GameHandler::handleGameTOK(Message *msg){
    LOG_DEBUG("GameHandler::handleGameTOK() - start");
    if(!checkLogged(msg->getSocket())){
        //todo negativní odpověď
        return;
    }
    if(!checkGame(msg->getMsg())){
        //todo negativní odpověď
        return;
    }

    Game *game = GameServices::getInst()->getGameByUid(msg->getMsg());
    User *user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());

    if(!(game->getPlayer(user)->hasToken())){
        //todo negativní odpověď
        return;
    }


    game->moveTokenToNextPlayer(user);
    msg->setMsg("OK");
    MessageQueue::sendInstance()->push_msg(msg);

    if(game->isEndOfRound()){
        LOG_DEBUG_PS("ENG OF ROUND IN",game->getUid().c_str())
        game->restartGame();
    }

    if(game->isEndOfGame()){
        LOG_DEBUG_PS("ENG OF GAME: ",game->getUid().c_str())
        game->finishGame();
        GameServices::getInst()->removeGame(game);
        game = NULL;
        return;
    }

}

/**
 * @brief GameHandler::handleGameCAR
 * @param msg
 */
void GameHandler::handleGameCAR(Message *msg){
    LOG_DEBUG("GameHandler::handleGameCAR() - start");
    if(!checkLogged(msg->getSocket())){
        LOG_DEBUG("Neni přihlášen");
        //todo negativní odpověď
        return;
    }

    User *user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());
    Game *game = user->getGame();

    if(game == NULL) {
        LOG_DEBUG("Neni nastaven odkaz na hru u hráče");
        return;
    }
    Player *player = game->getPlayer(user);

    if(player == NULL){
        LOG_DEBUG("Neni členem hry na kterou byl předán odkaz");
        return;
    }

    if(!player->hasToken()){
        player->sendCards();
        return;
    }

    bool result = game->giveCard(player);
    if(result == false){
        player->sendCards();
    }
}


/**
 * @brief GameHandler::handleGamePLA
 * @param msg
 */
void GameHandler::handleGamePLA(Message *msg){
    LOG_DEBUG("GameHandler::handleGamePLA() - start");
    if(!checkLogged(msg->getSocket())){
        LOG_DEBUG("Neni přihlášen");
        //todo negativní odpověď
        return;
    }

    User *user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());
    Game *game = user->getGame();

    if(game == NULL) {
        LOG_DEBUG("Neni nastaven odkaz na hru u hráče");
        return;
    }
    Player *player = game->getPlayer(user);

    if(player == NULL){
        LOG_DEBUG("Neni členem hry na kterou byl předán odkaz");
        return;
    }

    if(!player->hasToken()){
        LOG_DEBUG("Hráč není na tahu");
        return;
    }

    GameCards cardToPlay = GameCards::none;
    GameCards cardTip = GameCards::none;
    Player *affectedPlayer = NULL;

    vector<std::string> messageParts;
    char tmpMsg[254];
    memset(tmpMsg, 0, 254 * sizeof(char));
    strcpy(tmpMsg, msg->getMsg().c_str());
    char *msgP = strtok(tmpMsg, "&");

    while (msgP != NULL) {
        std::string s(msgP);
        messageParts.push_back(s);
        msgP = strtok(NULL, "&");
    }

    if(messageParts.size() >= 1){
        int card = std::stoi(messageParts[0]);
        cardToPlay = GameDeck::getCardByInt(card);
    }

    if(messageParts.size() >= 2){
        std::string userId = messageParts[1];
        affectedPlayer = game->getPlayer(UserDatabase::getInstance()->getUserById(userId));
    }

    if(messageParts.size() >= 3){
        int card = std::stoi(messageParts[2]);
        cardTip = GameDeck::getCardByInt(card);
    }

    bool result;
    std::string resultS = game->playCard(&result, cardToPlay, player, affectedPlayer, cardTip);
    if(result == false){
        std::string msgS = "CANCEL";
        msgS += "&&";
        msgS += std::to_string(cardToPlay);
        msgS += "&&";
        msgS += resultS;
        msg->setMsg(msgS);
        MessageQueue::sendInstance()->push_msg(msg);
        if(resultS.compare("WRONG") == 0 || resultS.compare("SAME") == 0){
            return;
        }
    }
    player->playCard(cardToPlay);
    game->sendCardToPlayers(cardToPlay, player);
    game->sendResult(player, affectedPlayer, cardToPlay, resultS);
    game->sendPlayersState(GameCards::none, NULL, NULL);
    game->sendCardsToPlayers();
}

/**
 * @brief GameHandler::handleGameCOD
 * @param msg
 */
void GameHandler::handleGameCOD(Message *msg){
    LOG_DEBUG("GameHandler::handleGameCOD() - start");

    User *user;
    std::string gameId = msg->getMsg();

    if(!checkLogged(msg->getSocket())){
        return;
    }

    user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());
    MSG_PS("Připojuji se na žádost uživatele", user->toString().c_str());

    if(!checkGame(gameId)){
        msg->setEvent(Event::NAK);
        msg->setMsg("NO ID");
        MessageQueue::sendInstance()->push_msg(msg);
        return;
    }

    if(user->getGame() != NULL){
        Game *game = user->getGame();
        MSG_PS("Uzivatel je jiz prihlasen do jine hry", game->toString().c_str());
        LOG_DEBUG_PS("Uzivatel je jiz prihlasen do jine hry", game->toString().c_str());
        msg->setEvent(Event::ACK);
        msg->setMsg(user->getGame()->getUid());
        MessageQueue::sendInstance()->push_msg(msg);
        return;
    }

    Game *game = GameServices::getInst()->getGameByUid(gameId);
    bool result = game->addPlayer(user);

    if(game->isStarted()){
        MSG_PS("Hra je rozehraná", game->toString().c_str());
        LOG_DEBUG_PS("Hra je rozehraná", game->toString().c_str());
        msg->setEvent(Event::NAK);
    } else if(!result){
        MSG_PS("Hra je obsazena", game->toString().c_str());
        LOG_DEBUG_PS("Hra je obsazena", game->toString().c_str());
        msg->setEvent(Event::NAK);
    } else {
        MSG_PS("Přihlašuji do hry :", game->toString().c_str());
        LOG_DEBUG_PS("Přihlašuji do hry :", game->toString().c_str());
        msg->setEvent(Event::ACK);
        user->setGame(game);

        for(int playerIndex = 0; playerIndex < game->getPlayer_count(); playerIndex++){
            std::string messageS = game->getUid();
            messageS += "&&";
            messageS += user->toNet();
            Message *msgP = new Message(game->getPlayer(playerIndex)->getUser()->getSocket()
                                      , MessageType::game
                                      , Event::NEP
                                      , messageS);
            MessageQueue::sendInstance()->push_msg(msgP);
        }
    }
    MessageQueue::sendInstance()->push_msg(msg);

    if(game->getPlayer_count() == 2){
        game->start();
    }
}

/**
 * @brief GameHandler::handleGameNEW
 * @param msg
 */
void GameHandler::handleGameNEW(Message *msg){
    LOG_DEBUG("GameHandler::handleGameNEW() - start");

    if(!checkLogged(msg->getSocket())){
        msg->setEvent(Event::NAK);
        MessageQueue::sendInstance()->push_msg(msg);
        return;
    }

    User *user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());
    MSG_PS("Vytvařím hru na žádost uživatele", user->toString().c_str());

    int round_count = 0;
    bool res = Utilities::readNumber(msg->getMsg(), &round_count);
    if(res == false && round_count <= 0){
        msg->setEvent(Event::NAK);
        MessageQueue::sendInstance()->push_msg(msg);
        return;
    }
    Game *g = GameServices::getInst()->createNewGame(round_count);
    msg->setEvent(Event::ACK);
    msg->setMsg(g->getUid());
    MessageQueue::sendInstance()->push_msg(msg);
}



