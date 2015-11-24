//module managing recieving messages
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>

#include "log.h"


#include <stdio.h>

const int OFFSET = 4;

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
                return -1;
        }
        else {
                log_info("Bind OK");
                return 1;
        }
}

void listen_netservice(int *server_sock){
	int n;
        int client_addr_len;
        struct sockaddr_in *remote_addr = malloc(sizeof (struct sockaddr_in));
        char c_buff[256];

	client_addr_len = sizeof( *remote_addr );

	while( 1 ) 
 	{
                log_info("Cekam na data.");
                
                //look at first 10 bytes of msg on size of next msg
		n = recvfrom(*server_sock, c_buff, 7, MSG_PEEK, (struct sockaddr*)remote_addr, &client_addr_len );
        	c_buff[n] = 0;                  
		n = atoi( c_buff+OFFSET ); //read lengt of message
		printf("BYTES TO READ: %d\n", n);


		n = recvfrom(*server_sock, c_buff, n, 0, (struct sockaddr*)remote_addr, &client_addr_len );
        	c_buff[n] = 0;	
		printf("READED BYTES: %d\n", n);


                log_info( "Zprava z klienta:");
		log_msg_in( c_buff );
/*
                log_info( "Server odesila:");
		log_msg_out( c_buff );
                n = sendto( *server_sock, c_buff, n, 0, (struct sockaddr*)remote_addr, client_addr_len );*/
        }
}

void send_netservice(int *server_sock, struct sockaddr_in* remote_addr, char *msg){

}

