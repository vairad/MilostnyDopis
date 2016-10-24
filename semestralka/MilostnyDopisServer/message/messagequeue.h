#ifndef MESSAGEQUEUE_H
#define MESSAGEQUEUE_H

#include "message.h"

#include <pthread.h>
#include <semaphore.h>
#include <queue>

#define MSGQ_SIZE 100

class MessageQueue
{
    pthread_mutex_t queue_lock;
    sem_t counter_sem_toServe;
    sem_t counter_sem_Size;

    MessageQueue();

    std::queue<Message *> message_queue;
    static MessageQueue *INSTANCE;


public:

    static MessageQueue *instance();
    Message *pop_msg();
    bool push_msg(Message *msg);
};

#endif // MESSAGEQUEUE_H
