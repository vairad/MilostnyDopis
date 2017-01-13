#ifndef SENDER_H
#define SENDER_H

#include <pthread.h>

#include "optcode.h"
#include "message/messagequeue.h"

class Sender
{
    static bool workFlag;
    static pthread_t *send_thread;

    static unsigned long sended_bytes_overflow;
    static unsigned long sended_bytes;

public:

    static void send_bytes(unsigned int byte_count);
    static void initialize();


    static void stop();
    static void *sendingStart(void *arg_ptr);
    static void startThread();
    static void joinThread();
    static void sendMessage(Message *msg);
    static unsigned long getSendedBytes();

private:
    static void fillType(char *msg, MessageType type);
    static void fillEvent(char *msg, Event event);
};

#endif // SENDER_H
