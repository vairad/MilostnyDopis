#include "messagequeue.h"

#include "log/log.h"

//====================================================================================================
MessageQueue *MessageQueue::INSTANCE = new MessageQueue();

MessageQueue *MessageQueue::instance()
{
    return INSTANCE;
}

//====================================================================================================

MessageQueue::MessageQueue()
{
    LOG_INFO("MessageQueue::MessageQueue()");

    int result = pthread_mutex_init(&queue_lock, NULL);    // inicializuj zámek
    if(result != 0){
        //todo handle errors
        LOG_ERROR("NEOŠETŘENÁ CHYBA");
    }
    LOG_TRACE("mutex inicializovan");


    result = sem_init(&counter_sem_toServe, 0, 0); // nastav semafor na prazdnou frontu
    if(result != 0){
        //todo handle errors
        LOG_ERROR("NEOŠETŘENÁ CHYBA");
    }
    LOG_TRACE("pocitadlo nezpracovanych zprav iniciaizovano");


    result = sem_init(&counter_sem_Size, 0, MSGQ_SIZE); // nastav semafor na prazdnou frontu
    if(result != 0){
        //todo handle errors
        LOG_ERROR("NEOŠETŘENÁ CHYBA");
    }
    LOG_TRACE("pocitadlo volných polozek ve fronte inicializovano");

}



bool MessageQueue::push_msg(Message *msg){
    LOG_INFO("MessageQueue::push_msg()");
    sem_wait(&counter_sem_Size);

    pthread_mutex_lock(&queue_lock);
    try
    {
        message_queue.push(msg);
    } catch (std::exception &e){
         LOG_ERROR_PS("Výjimka při vkládání do fronty zpráv", e.what());
         return false;
    }
    pthread_mutex_unlock(&queue_lock);

    sem_post(&counter_sem_toServe);
    return true;
}

Message *MessageQueue::pop_msg(void){
    LOG_INFO("MessageQueue::pop_msg()");
    Message *msg = NULL;
    sem_wait(&counter_sem_toServe);

    pthread_mutex_lock(&queue_lock);
    try
    {
        msg = message_queue.front();
        message_queue.pop();
    } catch (std::exception &e ){
        LOG_ERROR_PS("Výjimka při vybírání z fronty zpráv", e.what());
    }

    pthread_mutex_unlock(&queue_lock);

    sem_post(&counter_sem_Size);
    return msg;
}
