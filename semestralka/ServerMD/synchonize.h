#ifndef SYNCHONIZE_H
#define SYNCHONIZE_H

#include <queue>
#include <semaphore.h>

#include "message_list.h"
//semafory kritickych sekci v netservice
sem_t rcv_cs, send_cs, free_cs;
sem_t msgs_in_count, msgs_out_count;

msg_queue msgs_in, msgs_out;

std::queue<msg> msgs_in_list, msgs_out_list;

void init_synchronize();
void my_free(void *);


#endif // SYNCHONIZE_H
