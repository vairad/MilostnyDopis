#ifndef MESSAGEHANDLER_H
#define MESSAGEHANDLER_H

#include <pthread.h>

#include "message/messagequeue.h"

class MessageHandler
{
    MessageHandler();
    static bool workFlag;
    static pthread_t *workers;
    static int worker_count;
public:

    static void initialize(int worker_count);
    static void stop();
    static void *messageHandlerStart(void *arg_ptr);
    static void startThreads();
    static void joinThreads();
    static void handleMessage(Message *msg);

private:
    static void handleTypeMessage(Message *msg);
    static void handleTypeLogin(Message *msg);

    static void handleLoginECH(Message *msg);
    static void handleLoginCOD(Message *msg);

};

#endif // MESSAGEHANDLER_H
