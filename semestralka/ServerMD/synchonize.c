#include <semaphore.h>

#include "synchonize.h"
#include "message_list.h"

void init_synchronize(){
    //inicializace semaforu v netservice
    sem_init(&rcv_cs, 0, 1);
    sem_init(&send_cs, 0, 1);
    sem_init(&free_cs, 0, 1);

    //inicializace semaforu producent konzument bufferu zprav
    sem_init(&msgs_in_count, 0, 0);
    sem_init(&msgs_out_count, 0, 0);

    init_msg_queue(&msgs_in);
    init_msg_queue(&msgs_out);
}

void my_free(void *ptr){
    sem_wait(&free_cs);
    free(ptr);
    sem_post(&free_cs);
}
