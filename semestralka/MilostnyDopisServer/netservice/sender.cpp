#include "sender.h"

#include <cstring>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <cstdlib>
#include <cerrno>


#include "netservice/optcode.h"
#include "log/log.h"
#include "optcode.h"
#include "errornumber.h"

bool Sender::workFlag;
pthread_t *Sender::send_thread;
unsigned long Sender::sended_bytes = 0;
unsigned long Sender::sended_bytes_overflow = 0;

unsigned long Sender::getSendedBytes()
{
    return sended_bytes;
}

void Sender::send_bytes(unsigned int byte_count)
{
    unsigned long old_val = sended_bytes;
    sended_bytes += byte_count;
    if (old_val < sended_bytes){
        sended_bytes_overflow ++;
    }
}

void Sender::initialize()
{
    workFlag = true;
    send_thread = (pthread_t *) malloc(sizeof(pthread_t));
    if(send_thread == NULL)
    {
        MSG("Nedostatek paměti pro vyvtoření odesílacího vlákna.... Ukončuji program.")
        LOG_ERROR("Nedostatek paměti pro vytvoření odesílacího vlákna");
        exit(THREAD_MEMORY_ERROR_SEN);
    }
}

void Sender::stop()
{
    Sender::workFlag = false;
    pthread_cancel(*send_thread);
}

void Sender::startThread()
{
    LOG_INFO("Sender::startThread()");
    int result = pthread_create(send_thread, NULL, &Sender::sendingStart, NULL); // NULL neni třeba parametr
    if(result)
    { // 0 = success
        LOG_ERROR_P1("Vlákno odesílání zpráv nebylo inicialiováno. chybová hodnota", result);
        MSG("Chyba při startování programu. Ukončuji program.");
        exit(THREAD_CREATION_ERROR_SEN);
    }
}

void Sender::joinThread()
{
    LOG_INFO("Sender::joinThread()");
    void *retval;
    pthread_join(*send_thread, &retval);
}

void Sender::sendMessage(Message *msg)
{
    char msgToNet[2048];
    int charCount = HEADER_CHAR_COUNT + msg->getMsg().size() + 1;
    memset(msgToNet, 0, 2048);


//    msgToNet = (char *) malloc(sizeof(char) * charCount );
//    if(msgToNet == NULL){
//        MSG("Nedostatek paměti pro odeslání zprávy. Ukončuji program.");
//        LOG_ERROR("Nedostatek paměti pro odeslání zprávy");
//        exit(NOT_ENOUGH_MEMORY_SEN);
//    }

    strcpy(msgToNet, MESSAGE_HEADER);
    char number[5] = "0000";
    sprintf(number, "%04d", charCount);

    strcpy(msgToNet + 3, number);

    fillType(msgToNet, msg->getType());
    fillEvent(msgToNet, msg->getEvent());

    msgToNet[CONTENT_OFFSET - 1] = '#';

    strcpy(msgToNet + CONTENT_OFFSET, msg->getMsg().c_str());

    int sended = send(msg->getSocket(), msgToNet, charCount , 0);

    Sender::send_bytes(sended);

    if(msg != NULL){
        delete msg;
        msg = NULL;
    }
}

void Sender::fillType(char *msg, MessageType type){
    char typeS[4] = {0,0,0,0};
    switch (type) {
    case MessageType::message:
        strcpy(typeS, OPT_MSG);
        break;
    case MessageType::login:
        strcpy(typeS, OPT_LOG);
        break;
    case MessageType::game:
        strcpy(typeS, OPT_GAM);
        break;
    case MessageType::servis:
        strcpy(typeS, OPT_SRV);
        break;
    case MessageType::unknown:
        LOG_ERROR("Nelze nastavit typ unknown");
        break;
    default:
        MSG("Nenadála situace kontaktuje autora programu. Konec Programu");
        LOG_ERROR("Neimplementovaný typ!!");
        exit(TYPE_NOT_IMPLEMENTED_SEN);
    }

    strcpy(msg + TYPE_OFFSET, typeS);
}

void Sender::fillEvent(char *msg, Event event){
    char eventS[4] = {0,0,0,0};
    switch (event) {
    case Event::ACK:
        strcpy(eventS, OPT_ACK);
        break;
    case Event::NAK:
        strcpy(eventS, OPT_NAK);
        break;
    case Event::ECH:
        strcpy(eventS, OPT_ECH);
        break;
    case Event::STA:
        strcpy(eventS, OPT_STA);
        break;
    case Event::CAR:
        strcpy(eventS, OPT_CAR);
        break;
    case Event::NEP:
        strcpy(eventS, OPT_NEP);
        break;
    case Event::TOK:
        strcpy(eventS, OPT_TOK);
        break;
    case Event::PLA:
        strcpy(eventS, OPT_PLA);
        break;
    case Event::PLS:
        strcpy(eventS, OPT_PLS);
        break;
    case Event::PTS:
        strcpy(eventS, OPT_PTS);
        break;
    case Event::RES:
        strcpy(eventS, OPT_RES);
        break;
    case Event::UNK:
        LOG_ERROR("Nelze nastavit typ unknown");
        break;
    default:
        MSG("Nenadála situace kontaktuje autora programu. Konec programu.");
        LOG_ERROR("Neimplementovaný event v Sender!!");
        exit(EVENT_NOT_IMPLEMENTED_SEN);
    }

    strcpy(msg + EVENT_OFFSET, eventS);
}

void *Sender::sendingStart(void *arg_ptr)
{
    while(workFlag){
        Message *msg = MessageQueue::sendInstance()->pop_msg();
        sendMessage(msg);
    }
}

