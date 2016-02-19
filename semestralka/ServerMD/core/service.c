#include <semaphore.h>
#include <errno.h>
#include <pthread.h>

#include "core/service.h"
#include "core/synchonize.h"
#include "test/log.h"
#include "netservice/messages.h"

#include <stdio.h>

int flag_service = 1;

int run_service(){
    while(flag_service){
        int boolean = 0;

      //koukni do přijatých zpráv
        boolean = sem_trywait(&msgs_in_count);
            if(boolean != -1){ // zabral jsi semafor proveďobsluhu
                msg poped;
                sem_wait(&rcv_cs);//////////////////////

                    pop_front_msg(&msgs_in, &poped);

                sem_post(&rcv_cs);/////////////////////

                serve_message(&poped);
            }

      //koukni na timeouty her pripadne posli zpravy znovu

            // pokud vypršely pošli znovu nové


        pthread_yield(); // vzdá se procesoru... :)
    }
}

int serve_message(msg *message){

    msg *ack = malloc(sizeof (struct MSG));
    create_ack(ack, message);

    sem_wait(&send_cs);/////////////////////////////
        push_back_msg(&msgs_out, &ack);
    sem_post(&send_cs);/////////////////////////////
}

void stop_service(){
    flag_service = 0;
}

