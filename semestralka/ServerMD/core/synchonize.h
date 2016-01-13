#ifndef SYNCHONIZE_H
#define SYNCHONIZE_H

#include <semaphore.h>

#include "netservice/message_list.h"
//semafory kritickych sekci v netservice
sem_t rcv_cs, send_cs, free_cs;
sem_t msgs_in_count, msgs_out_count;

msg_queue msgs_in, msgs_out;

void init_synchronize();


#endif // SYNCHONIZE_H
