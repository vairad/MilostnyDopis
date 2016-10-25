#include "sender.h"

#include "errornumber.h"
#include "log/log.h"

bool Sender::workFlag;
pthread_t *Sender::send_thread;

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
    MSG(msg->getMsg().c_str());
}

void *Sender::sendingStart(void *arg_ptr)
{
    while(workFlag){
        Message *msg = MessageQueue::sendInstance()->pop_msg();
        sendMessage(msg);
    }
    return NULL;
}

