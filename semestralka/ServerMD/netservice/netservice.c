//module managing recieving messages
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <pthread.h>

#include <errno.h>

#include "test/log.h"
#include "netservice/netservice.h"
#include "core/synchonize.h"


#include <stdio.h>


const int OFFSET = 4;
const int BUFFER_SIZE = 256;

int trash_bytes = 0;
int send_bytes = 0;
int recieved_bytes = 0;

int run_flag_netservice = 1;

/////////////////////////////////////////////////////////////////////// NETSERVICE FUNCTIONS

int prepare_socket(int port, int *server_sock){
        int server_addr_len;
        struct sockaddr_in local_addr;

        //setup socket
        *server_sock = socket( AF_INET, SOCK_DGRAM, 0 );
        //set memory on local addr to 0
        memset(&local_addr, 0, sizeof(struct sockaddr_in));

        //prepare local addr
        local_addr.sin_family = AF_INET;
        local_addr.sin_addr.s_addr = htons(INADDR_ANY) ;
        local_addr.sin_port = htons(port);

        server_addr_len = sizeof( local_addr );

        if( bind( (*server_sock), ((struct sockaddr *)&local_addr), server_addr_len ) != 0 )
        {
                log_error("Bind ERR");
                return 0;
        }
        else {
                log_info("Bind OK");
                return 1;
        }
}

///////////////////////////////THREAD FUNCTION LISTEN
void *listen_netservice(void *server_sock_p){
        int *server_sock = (int *)server_sock_p;
        int n;
        socklen_t client_addr_len;
        struct sockaddr_in *remote_addr;
        char c_buff[BUFFER_SIZE];

	client_addr_len = sizeof( *remote_addr );

    log_info("RECV: Prijimaci vlakno bezi");
	while( run_flag_netservice ) 
 	{
        remote_addr = malloc(sizeof (struct sockaddr_in));
        log_info("RECV: Cekam na data.");

        //look at first 10 bytes of msg on size of next msg
		n = recvfrom(*server_sock, c_buff, 7, MSG_PEEK, (struct sockaddr*)remote_addr, &client_addr_len );
        c_buff[n] = 0; //nastav konec retezce

        //	printf( "READED BYTES : %d\n", n);

        errno = 0;
        int i = atoi( c_buff+OFFSET ); //zjisti velikost zprávy

        if(errno != 0){ // pokud narazis na chybu zahod vse prijate a pokracuj v naslouchani
            trash_bytes += recvfrom(*server_sock, c_buff, i, 0, (struct sockaddr*)remote_addr, &client_addr_len );
            continue;
        }

		n = recvfrom(*server_sock, c_buff, i, 0, (struct sockaddr*)remote_addr, &client_addr_len );
        c_buff[n] = 0; //nastav konec retezce

        log_info("RECV: Prijata zprava:");
        log_msg_in(c_buff);


         sem_wait(&msgs_in_empty); // zmensi velikost bufferu pokud neni misto blokuj se
        //start kriticka sekce zapisu do bufferu       
        sem_wait(&rcv_cs);///////////////////////////////////////////
            if(push_back(&msgs_in, c_buff, remote_addr, client_addr_len)){
                sem_post(&msgs_in_count);
            }else{
                sem_post(&msgs_in_empty); // nepovedlo se přidat zprávu zvyš velikost bufferu
            }
        sem_post(&rcv_cs);//////////////////////////////////////////
        //end kriticka sekce zapisu do bufferu

    }
    return NULL;
}

//////////////////////////////////THREAD FUNCTION SEND
void *send_netservice(void *server_sock_p){
    int *server_sock = (int *)server_sock_p;
    size_t n;
    socklen_t client_addr_len;
    struct sockaddr_in *remote_addr;
    char *c_buff;
    int logic = 0;

    log_info("SEND: Odesilaci vlakno bezi");

    sem_wait(&msgs_out_count); // zastav se dokud nebude nová zpráva
    while (run_flag_netservice){


        log_info("SEND: Pripravuji odeslani dat.");

        sem_wait(&send_cs);///////////////////////////////////////
            log_info("SEND: KS seznamu zprav.");
            logic = pop_front(&msgs_out, &c_buff, &remote_addr, &client_addr_len);

            if(logic != 1){
                sem_post(&msgs_out_count); // pokud zpráva nebyla vybraná opet zvys semafor
            }else{
                sem_post(&msgs_out_empty); //zvyš velikost bufferu
            }
        sem_post(&send_cs);//////////////////////////////////////

       log_info("SEND: Server odesila");
       log_msg_out( c_buff );

       n = strlen(c_buff);

       printf("INFO  : SEND: strlen: %ld \n", n);

       c_buff[n] = 0;
       n++;
       c_buff[n] = 0;

       n = sendto( *server_sock, c_buff, n, 0, (struct sockaddr*)remote_addr, client_addr_len );
       send_bytes += n;
       printf("INFO   : SEND: sendto: %ld \n", n);

       log_info("SEND: Server odeslal zpravu");
       sem_wait(&msgs_out_count); // zastav se dokud nebude nová zpráva
    }
    log_info("SEND: Odesilaci vlakno ukonceno");
    return NULL;
}

//////////////////////////////////////////////STOP NETSERVICE
void stop_netservice(){
    run_flag_netservice = 0; // podminka provadeni cyklu while
    pthread_cancel(reciever); // zrus přijimaci vlakno
    sem_post(&msgs_out_count); // uvolni odesilaci vlakno, aby vstoupilo do podminky

}

//////////////////////////////////////////////START THREADS OF NETSERVICE
void start_netservice(int *server_sock){
    pthread_create(&reciever, NULL, &listen_netservice, server_sock);
    pthread_create(&sender, NULL, &send_netservice, server_sock);

}

//////////////////////////////////////////////JOIN THREADS OF NETSERVICE
void join_netservice(){
  //  pthread_join(reciever, NULL);
    pthread_join(sender, NULL);

    printf("INFO    : SENDED BYTES %d \n", send_bytes);
    printf("INFO    : RECIEVED TRASH BYTES %d \n", trash_bytes);
    printf("INFO    : RECIEVED BYTES %d \n", recieved_bytes);
}


