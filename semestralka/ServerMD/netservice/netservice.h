//module managing recieving messages
#ifndef NETSERVICE_ASFWO5687951
#define NETSERVICE_ASFWO5687951

#include <pthread.h>

#include "core/synchonize.h"

pthread_t reciever, sender;

int prepare_socket(int port, int *server_sock);
void *listen_netservice(void *server_sock_p);
void *send_netservice(void *server_sock_p);
void stop_netservice();

void start_netservice(int *server_sock);
void join_netservice();

#endif

