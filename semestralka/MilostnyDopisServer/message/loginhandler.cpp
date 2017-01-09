#include "loginhandler.h"

#include "errornumber.h"
#include "log/log.h"

#include "message/messagequeue.h"

#include "users/userdatabase.h"
#include "game/gameservices.h"

#include "util/utilities.h"

void LoginHandler::handleTypeLogin(Message *msg)
{
   Event type = msg->getEvent();
    switch (type){
    case Event::ECH : //echo event
        handleLoginECH(msg);
        break;
    case Event::COD:
        handleLoginCOD(msg);
        break;
    case Event::OUT:
        handleLoginOUT(msg);
        break;
    case Event::UNK :
    default:
        //todo impl

        break;
    }
}

void LoginHandler::handleLoginECH(Message *msg)
{
    LOG_DEBUG("MessageHandler::handleLoginECH() - start");
    MSG_PS("Přihlašuji uživatele", msg->getMsg().c_str());

    User *user;
    std::string id;
    if(UserDatabase::getInstance()->hasSocketUser(msg->getSocket())){
        MSG_PD("Na tomto socketu je již vytvořen uživaltel", msg->getSocket());
        user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());
        id = user->getUID();
        msg->setMsg(*user->getNickname());
        msg->setEvent(Event::NAK);
    } else {
        user = new User(new std::string(msg->getMsg()), msg->getSocket());
        id = UserDatabase::getInstance()->addUser(user, msg->getSocket());
        msg->setEvent(Event::ACK);
    }

    id += "&&";
    id += msg->getMsg();
    msg->setMsg(id);
    MessageQueue::sendInstance()->push_msg(msg);
}

void LoginHandler::handleLoginCOD(Message *msg)
{
    LOG_DEBUG("MessageHandler::handleLoginCOD() - start");
    MSG_PS("Přihlašuji uživatele s id", msg->getMsg().c_str());

    User *user;
    std::string id = msg->getMsg();
    if(UserDatabase::getInstance()->hasSocketUser(msg->getSocket())){
        MSG_PD("Na tomto socketu je již vytvořen uživaltel", msg->getSocket());
        user = UserDatabase::getInstance()->getUserBySocket(msg->getSocket());
        id = user->getUID();
        msg->setMsg(*user->getNickname());
        msg->setEvent(Event::NAK);
    } else {
        if(!UserDatabase::getInstance()->existUserID(id)){
            MSG("Neexistujici ID");
            LOG_DEBUG_PS("Neexistujici ID", id.c_str());
            msg->setEvent(Event::NAK);
            msg->setMsg("NO ID");
            MessageQueue::sendInstance()->push_msg(msg);
            return;
        }
        user = UserDatabase::getInstance()->getUserById(id);
        UserDatabase::getInstance()->setSocketUser(id, msg->getSocket());
        user->setSocket(msg->getSocket());
        msg->setEvent(Event::ACK);
    }

    id += "&&";
    id += user->getNickname()->c_str();
    msg->setMsg(id);
    MessageQueue::sendInstance()->push_msg(msg);
}

void LoginHandler::handleLoginOUT(Message *msg)
{
    LOG_DEBUG("MessageHandler::handleLoginOUT() - start");
    MSG_PS("Odhlašuji uživatele s id", msg->getMsg().c_str());
    bool result = UserDatabase::getInstance()->removeUser(msg->getMsg());
    string msgS = msg->getMsg();
    if(result){
        msg->setEvent(Event::ACK);
        msg->setMsg("OUT&&" + msgS);
    }else{
        msg->setEvent(Event::NAK);
        msg->setMsg("OUT&&" + msgS);
    }
}
