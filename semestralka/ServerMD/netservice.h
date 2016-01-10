//module managing recieving messages
#ifndef NETSERVICE_ASFWO5687951
#define NETSERVICE_ASFWO5687951

#include <pthread.h>

#include "synchonize.h"

pthread_t reciever, sender;

int prepare_socket(int port, int *server_sock);
void listen_netservice(int *server_sock);
void start_netservice(int *server_sock);

#endif

