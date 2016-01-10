#include <stddef.h>

#include "log.h"
#include "message_list.h"
#include "synchonize.h"


void init_msg_queue(msg_queue *queue){
    queue->first = NULL;
    queue->last = NULL;
}

int push_back(msg_queue *queue, char *msg_in, struct sockaddr_in *addr,  socklen_t addr_len){
    log_info("in push_back()");
    if(queue == NULL){
        log_error("Nebyl predan odkaz na frontu v push_back()");
        return 0;
    }
    if(msg_in == NULL){
        log_error("Nebyl predan odkaz na zpravu v push_back()");
        return 0;
    }
    if(addr == NULL){
        log_error("Nebyl predan odkaz na adresu v push_back()");
        return 0;
    }

    //priprava noveho uzlu
    msg *new_msg;
    new_msg = malloc(sizeof (struct MSG));

    new_msg->msg = malloc(strlen(msg_in)+1);
    strcpy(new_msg->msg, msg_in);

    new_msg->addr = addr;
    new_msg->addr_len = addr_len;


    //razeni do fronty
    if(queue->last == NULL){ // pokud je konec NULL znamena to, že je fronta zprav prazdna
        queue->first = new_msg;
        queue->last = new_msg;
        new_msg->next = NULL;
        new_msg->prev = NULL;
    }else{                  //pokud neni prazdna
        new_msg->prev = queue->last; //novy dostane odkaz na stary konec
        queue->last->next = new_msg; //stary konec dostane odkaz na novy

        new_msg->next = NULL;   // novy ukazije Nikam
        queue->last = new_msg; // konec fronty ukazuje na novy konec
    }
    log_info("OK out push_back()");
    return 1;
}

int pop_front(msg_queue *queue, char **msg_out, struct sockaddr_in **addr,  socklen_t *addr_len){
    log_info("in pop_front()");
    if(queue == NULL){
        log_error("Nebyl predan odkaz na frontu v pop_front()");
        return 0;
    }
    if(msg_out == NULL){
        log_error("Nebyl predan odkaz na zpravu v pop_front()");
        return 0;
    }
    if(addr == NULL){
        log_error("Nebyl predan odkaz na adresu v pop_front()");
        return 0;
    }
    if(addr == NULL){
        log_error("Nebyl predan odkaz na delku adresy v pop_front()");
        return 0;
    }

    msg *msg_pop = queue->first; //zachovej si odkaz

    if(msg_pop == NULL){
        log_error("Fronta je prazdna v pop_front()");
        return 0;
    }

    pop(queue); //zahod predchozi prvek

    *msg_out = msg_pop->msg;
    *addr = msg_pop->addr;
    *addr_len = msg_pop->addr_len;

    free(msg_pop);

    log_info("OK out pop_front()");
    return 1;
}


void pop(msg_queue *queue){
    log_info("in pop()");
    if(queue == NULL){
        log_error("Nebyl predan odkaz na frontu v pop()");
        return;
    }

    if(queue->first == queue->last){ // pokud ukazuje na stejny prvek je posledni nebo je fronta prazdna
        queue->first = NULL;
        queue->last = NULL;
    }else{ // jinak posun ukazatel o jeden zpet
        queue->first = queue->first->prev;
        queue->first->next = NULL;
    }
    log_info("OK out pop()");
}
