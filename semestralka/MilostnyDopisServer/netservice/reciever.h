#ifndef RECIEVER_H
#define RECIEVER_H

#include <pthread.h>

#include "netservice/netstructure.h"

#define REPEATED_ERRORS_LIMIT 4

class Reciever
{
    int run_flag;
    int error_counter;

    NetStructure *netStructure_p;



public:
    Reciever(NetStructure *net);


    static pthread_t *listen_thread_p;

    static void initThreads(Reciever *service);
    static void *listenerStart(void *service_ptr);
private:
    void serve_messages();
    void check_select_error(int error_val);
    void handle_socket_activity(fd_set *sockets);
    void handle_message();
};

#endif // RECIEVER_H
