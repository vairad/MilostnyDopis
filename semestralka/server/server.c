#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>

#include "log.h"
#include <stdio.h>

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

/*****************************************
* Check if port number is from range 1 - 65535. 
* If number is not correct change port number to 1234.
* return 1 or -1  1 if number is correct
*/
int check_port_number(int * port){
	if(*port > 0 && *port < 65535){
	return 1;
	}
	*port = 1234; //setup default value
	log_warn("Default port 1234 set");
	return -1;
}

int main(void)
{
	int server_sock, client_sock, n;
	int client_addr_len;
	struct sockaddr_in remote_addr;
	char c_buff[256];

	int port = 1234;

	prepare_socket(port, &server_sock);

	while( 1 )
	{
		log_info("Server ceka na data.");
		
		client_addr_len = sizeof( remote_addr );
		n = recvfrom(server_sock, c_buff, 256, 0, (struct sockaddr*)&remote_addr, &client_addr_len );
	
		c_buff[n] = 0;			

		log_info( "Pripojil se klient" );
		printf( "Klient poslal = %s\n", c_buff );

		sleep(5);

		printf( "Server odesila = %s\n", c_buff );
		n = sendto( server_sock, c_buff, 256, 0, (struct sockaddr*)&remote_addr, client_addr_len );

		close( client_sock );

	}
}

