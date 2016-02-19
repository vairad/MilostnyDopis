#ifndef MESSAGE_LIST_H
#define MESSAGE_LIST_H


#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <stddef.h>

typedef struct MSG{
    char *msg;
    struct sockaddr_in *addr;
    socklen_t addr_len;

    struct MSG *next;
    struct MSG *prev;
} msg;


/* structure message list */
typedef struct MSG_QUEUE {
    msg *first;
    msg *last;

} msg_queue;


void init_msg_queue(msg_queue *queue);
void pop(msg_queue *queue);
int push_back(msg_queue *queue, char *msg, struct sockaddr_in *addr,  socklen_t addr_len);
int push_back_msg(msg_queue *queue, msg *msg_in);
int pop_front(msg_queue *queue, char **msg, struct sockaddr_in **addr,  socklen_t *addr_len);
int pop_front_msg(msg_queue *queue, msg *msg_out);


int copy_address(msg *message_dst, msg *message_src);
int create_ack(msg *ack, msg *recv);

#endif // MESSAGE_LIST_H
