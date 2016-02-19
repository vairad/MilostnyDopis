#include <stddef.h>

#include "test/log.h"
#include "netservice/messages.h"
#include "core/synchonize.h"


void init_msg_queue(msg_queue *queue){
    queue->first = NULL;
    queue->last = NULL;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
int push_back(msg_queue *queue, char *msg_in, struct sockaddr_in *addr,  socklen_t addr_len){
    log_trace("in push_back()");
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

    new_msg->msg = malloc(strlen(msg_in));
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
    log_trace("OK out push_back()");
    return 1;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
int push_back_msg(msg_queue *queue, msg *msg_in){
    log_trace("in push_back()");
    if(queue == NULL){
        log_error("Nebyl predan odkaz na frontu v push_back()");
        return 0;
    }
    if(msg_in == NULL){
        log_error("Nebyl predan odkaz na strukturu zpravy v push_back()");
        return 0;
    }
    if(msg_in->msg == NULL){
        log_error("Nebyl predan odkaz na zpravu v push_back()");
        return 0;
    }
    if(msg_in->addr == NULL){
        log_error("Nebyl predan odkaz na adresu v push_back()");
        return 0;
    }

    //priprava noveho uzlu
    msg *new_msg;
    new_msg = malloc(sizeof (struct MSG));

    new_msg->msg = malloc(strlen(msg_in->msg));
    strcpy(new_msg->msg, msg_in->msg);

    new_msg->addr = msg_in->addr;
    new_msg->addr_len = msg_in->addr_len;


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
    log_trace("OK out push_back()");
    return 1;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
int pop_front_msg(msg_queue *queue, msg *msg_out){
    return pop_front(queue, &msg_out->msg, &msg_out->addr, &msg_out->addr_len);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
int pop_front(msg_queue *queue, char **msg_out, struct sockaddr_in **addr,  socklen_t *addr_len){
    log_trace("in pop_front()");
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

    log_trace("OK out pop_front()");
    return 1;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
void pop(msg_queue *queue){
    log_trace("in pop()");
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
    log_trace("OK out pop()");
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////
int copy_address(msg *message_dst, msg *message_src){
    if(message_dst == NULL){
        log_error("Nebyl predan odkaz na cíl copy_address()");
        return 0;
    }
    if(message_src == NULL){
        log_error("Nebyl predan odkaz na zdroj copy_address()");
        return 0;
    }
    message_dst->addr = message_src->addr;
    message_dst->addr_len = message_src->addr_len;
    return 1;
}

int create_ack(msg *ack, msg *recv){
     copy_address(ack, recv);

     char tmp[] = "ACK#029#000000000#000000000##";
     memcpy( tmp + 18, &recv->msg[8], 9 );


     ack->msg = malloc(strlen(tmp));
     strcpy(ack->msg, tmp);

     log_trace(tmp);
     log_trace(ack->msg);

     return 1;
}
