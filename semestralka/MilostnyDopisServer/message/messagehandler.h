#ifndef MESSAGEHANDLER_H
#define MESSAGEHANDLER_H

#include <pthread.h>

#include "message/messagequeue.h"

class MessageHandler
{
    static bool workFlag;
public:


    static void stop();
    static void initThreads();
    static void *messageHandlerStart(void *arg_ptr);
    MessageHandler();
};

#endif // MESSAGEHANDLER_H
