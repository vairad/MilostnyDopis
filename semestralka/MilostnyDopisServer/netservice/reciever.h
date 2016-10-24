#ifndef RECIEVER_H
#define RECIEVER_H

#include <pthread.h>

#include "message/messagequeue.h"
#include "netservice/netstructure.h"

#define REPEATED_ERRORS_LIMIT 4
#define MSG_LEN_OFFSET 3
#define MESSAGE_BUFFER_SIZE 2048

#define OPT_SIZE 3
#define TYPE_OFFSET 7
#define EVENT_OFFSET 10
#define CONTENT_OFFSET 14


class Reciever
{
    int run_flag;
    int error_counter;

    char message_buffer[MESSAGE_BUFFER_SIZE];

    NetStructure *netStructure_p;



public:
    Reciever(NetStructure *net);


    void stop();

    static pthread_t *listen_thread_p;

    static void initThreads(Reciever *service);
    static void *listenerStart(void *service_ptr);
private:
    void serve_messages();
    void check_select_error(int error_val);
    void handle_socket_activity(fd_set *sockets);
    void handle_message(int socket);
    void create_message();
    MessageType choose_type(char *opt);
    Event choose_event(char *opt);
};

#endif // RECIEVER_H
