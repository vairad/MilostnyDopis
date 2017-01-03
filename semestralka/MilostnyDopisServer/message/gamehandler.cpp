#include "gamehandler.h"

#include "errornumber.h"
#include "log/log.h"

#include "message/messagequeue.h"

#include "users/userdatabase.h"
#include "game/gameservices.h"

#include "util/utilities.h"

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

    int round_count;
    bool res = Utilities::readNumber(msg->getMsg(), &round_count);
    if(res == false && round_count <= 0){
        msg->setEvent(Event::NAK);
        MessageQueue::sendInstance()->push_msg(msg);
    }
    Game *g = GameServices::getInst()->createNewGame(round_count);
    msg->setEvent(Event::ACK);
    msg->setMsg(g->getUid());
    MessageQueue::sendInstance()->push_msg(msg);
}



