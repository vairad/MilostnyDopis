#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdlib.h>

int prepare_socket(int port, char *listen_addr, int *server_sock){
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
		printf("Bind ERR\n");
                return -1;
	} 
	else {
		printf("Bind OK\n");
		return 1;
	}	
}

int main(void)
{
	int server_sock, client_sock, n;
	int client_addr_len;
	struct sockaddr_in remote_addr;
	char c_buff[256];

	char *str = "127.0.0.1";
	int port = 1234;

	prepare_socket(port, str, &server_sock);

	while( 1 )
	{
		printf( "Server ceka na data\n" );
		
		client_addr_len = sizeof( remote_addr );
		n = recvfrom(server_sock, c_buff, 256, 0, (struct sockaddr*)&remote_addr, &client_addr_len );
	
		c_buff[n] = 0;			

		printf( "Pripojil se klient\n" );
		printf( "Klient poslal = %s\n", c_buff );

		sleep(5);

		printf( "Server odesila = %s\n", c_buff );
		n = sendto( server_sock, c_buff, 256, 0, (struct sockaddr*)&remote_addr, client_addr_len );

		close( client_sock );

	}
}

