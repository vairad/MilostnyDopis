#ifndef RECIEVER_H
#define RECIEVER_H

#include <pthread.h>

#include "netservice/netstructure.h"

class Reciever
{
    int port_number;
    int server_socket;
    int run_flag;

    NetStructure *netStructure_p;

    static pthread_t *listen_thread_p;

public:
    Reciever(NetStructure *net);

    static void initThreads(Reciever *service);
    static void *listenerStart(void *service_ptr);
};

#endif // RECIEVER_H
