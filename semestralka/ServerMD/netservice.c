//module managing recieving messages
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>

#include <pthread.h>

#include "log.h"
#include "netservice.h"
#include "synchonize.h"


#include <stdio.h>


const int OFFSET = 4;
const int BUFFER_SIZE = 256;

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


void listen_netservice(int *server_sock){
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

        int i = atoi( c_buff+OFFSET ); //zjisti velikost zprávy
        //printf("Zjistena velikost %d\n", i);

		n = recvfrom(*server_sock, c_buff, i, 0, (struct sockaddr*)remote_addr, &client_addr_len );
	
        log_info("RECV: Prijata zprava:");
        log_msg_in( c_buff );

        //start kriticka sekce zapisu do bufferu
        sem_wait(&rcv_cs);
            if(push_back(&msgs_in, c_buff, remote_addr, client_addr_len)){
                sem_post(&msgs_in_count);
            }
        sem_post(&rcv_cs);
        //end kriticka sekce zapisu do bufferu

    }
}

void send_netservice(int *server_sock){

    int n;
    socklen_t client_addr_len;
    struct sockaddr_in *remote_addr;
    char *c_buff;
    int logic = 0;

    log_info("SEND: Odesilaci vlakno bezi");
    while (run_flag_netservice){

        sem_wait(&msgs_in_count);
        log_info("SEND: Pripravuji odeslani dat.");

        sem_wait(&rcv_cs);
            log_info("SEND: KS seznamu zprav.");
            logic = pop_front(&msgs_in, &c_buff, &remote_addr, &client_addr_len);

            printf("VYBRANO = %d \n", logic);
            if(logic == 1){
                printf("%s \n", c_buff);
            }else{
                sem_post(&msgs_in_count); // pokud zpráva nebyla vybraná opet zvys semafor
            }
        sem_post(&rcv_cs);

       log_info("SEND: Server odesila");
       log_msg_out( c_buff );
       n = strlen(c_buff) + 1;
       n = sendto( *server_sock, c_buff, n, 0, (struct sockaddr*)remote_addr, client_addr_len );
       printf("odeslano: %d \n", n);
    }

}

void start_netservice(int *server_sock){
    pthread_create(&reciever, NULL, &listen_netservice, server_sock);
    pthread_create(&sender, NULL, &send_netservice, server_sock);
    sleep(100); // wait until threads create

    pthread_join(reciever, NULL);
    pthread_join(sender, NULL);
}


