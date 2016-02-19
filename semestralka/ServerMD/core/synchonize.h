#ifndef SYNCHONIZE_H
#define SYNCHONIZE_H

#include <semaphore.h>

#include "netservice/messages.h"
//semafory kritickych sekci v netservice
sem_t rcv_cs, send_cs, free_cs;
sem_t msgs_in_count, msgs_out_count;
sem_t msgs_in_empty, msgs_out_empty;

msg_queue msgs_in, msgs_out;

void init_synchronize();


#endif // SYNCHONIZE_H
