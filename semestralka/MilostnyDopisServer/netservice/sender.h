#ifndef MESSAGEHANDLER_H
#define MESSAGEHANDLER_H

#include <pthread.h>

#include "message/messagequeue.h"

class Sender
{
    static bool workFlag;
    static pthread_t *send_thread;
public:

    static void initialize();


    static void stop();
    static void *sendingStart(void *arg_ptr);
    static void startThread();
    static void joinThread();
    static void sendMessage(Message *msg);
};

#endif // MESSAGEHANDLER_H
