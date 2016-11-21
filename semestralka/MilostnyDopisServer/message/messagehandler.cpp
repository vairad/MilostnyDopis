#include "messagehandler.h"

#include "errornumber.h"
#include "log/log.h"

#include "netservice/netstructure.h"

#include "users/userdatabase.h"

bool MessageHandler::workFlag;
pthread_t *MessageHandler::workers;
int MessageHandler::worker_count;

MessageHandler::MessageHandler()
{
    // no code here
}

void MessageHandler::initialize(int worker_count)
{
    MessageHandler::workFlag = true;
    MessageHandler::worker_count = worker_count;
    workers = (pthread_t *) malloc(sizeof(pthread_t) * worker_count);
    if(workers == NULL)
    {
        MSG("Nedostatek paměti pro vyvtoření pracovních vláken.... Ukončuji program.")
        LOG_ERROR("Nedostatek paměti pro vytvoření pracovních vláken");
        exit(THREAD_MEMORY_ERROR_REC);
    }
}

void MessageHandler::stop()
{
    MessageHandler::workFlag = false;
}

void MessageHandler::startThreads()
{
    LOG_INFO("MessageHandler::startThreads()");
    for(int thread_index = 0; thread_index < MessageHandler::worker_count; thread_index++){
        int result = pthread_create(workers + thread_index, NULL, &MessageHandler::messageHandlerStart, NULL); // NULL neni třeba parametr
        if(result)
        { // 0 = success
            LOG_ERROR_P1("Vlákno zpracování zprávn nebylo inicialiováno. chybová hodnota", result);
            MSG("Chyba při startování programu. Ukončuji program.");
            exit(THREAD_CREATION_ERROR_MSG);
        }
    }
}

void MessageHandler::joinThreads()
{
    LOG_INFO("MessageHandler::joinThreads()");
    for(int thread_index = 0; thread_index < MessageHandler::worker_count; thread_index++){
        int result = pthread_create(workers + thread_index, NULL, &MessageHandler::messageHandlerStart, NULL); // NULL neni třeba parametr
        if(result)
        { // 0 = success
            MSG("Chyba při startování programu. Ukončuji program.");
            LOG_ERROR_P1("Vlákno zpracování zprávn nebylo inicialiováno. chybová hodnota", result);
            exit(THREAD_CREATION_ERROR_MSG);
        }
    }
}

void MessageHandler::handleMessage(Message *msg)
{
    MessageType type = msg->getType();
    switch (type){
    case MessageType::message :
        handleTypeMessage(msg);
        break;
    case MessageType::login :
        handleTypeLogin(msg);
        break;
    case MessageType::game :
        //todo impl
        break;
    case MessageType::servis :
        //todo impl
        break;
    case MessageType::unknown :
    default:
        break;
    };
}

void MessageHandler::handleTypeMessage(Message *msg)
{
   Event type = msg->getEvent();
    switch (type){
    case Event::ECH : //echo event
        MSG(msg->getMsg().c_str());
        msg->setEvent(Event::ACK);
        MessageQueue::sendInstance()->push_msg(msg);
        break;        
    case Event::UNK :
    default:
        //todo impl

        break;
    }
}

void MessageHandler::handleTypeLogin(Message *msg)
{
   Event type = msg->getEvent();
    switch (type){
    case Event::ECH : //echo event
        handleLoginECH(msg);
        break;
    case Event::COD:
        handleLoginCOD(msg);
    case Event::UNK :
    default:
        //todo impl

        break;
    }
}

void *MessageHandler::messageHandlerStart(void *arg_ptr)
{
    while(workFlag){
        Message *msg = MessageQueue::recieveInstance()->pop_msg();
        handleMessage(msg);
    }
}


void MessageHandler::handleLoginECH(Message *msg)
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

void MessageHandler::handleLoginCOD(Message *msg)
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
        user = UserDatabase::getInstance()->getUserById(id);
        UserDatabase::getInstance()->setSocketUser(id, msg->getSocket());
        msg->setEvent(Event::ACK);
    }

    id += "&&";
    id += user->getNickname()->c_str();
    msg->setMsg(id);
    MessageQueue::sendInstance()->push_msg(msg);
}
