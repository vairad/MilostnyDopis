#ifndef NETSERVICE_H
#define NETSERVICE_H

#include <pthread.h>


class Netservice
{
    int port_number;
    int server_socket;
    int run_flag;

    static pthread_t *listen_thread;

public:
    Netservice();

    static void initThreads(Netservice *service);
    static void *listenerStart(void *service_ptr);
};

#endif // NETSERVICE_H
