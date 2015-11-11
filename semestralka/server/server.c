#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdlib.h>

int main(void)
{
	int server_sock, client_sock, n;
	int server_addr_len, client_addr_len;
	struct sockaddr_in local_addr;
	struct sockaddr_in remote_addr;
	char c_buff[256];


	server_sock = socket( AF_INET, SOCK_DGRAM, 0 );

	memset(&local_addr, 0, sizeof(struct sockaddr_in));

	local_addr.sin_family = AF_INET;
	local_addr.sin_addr.s_addr = inet_addr( "127.0.0.1" );
	local_addr.sin_port = htons(1234);

	server_addr_len = sizeof( local_addr );

	if( bind( server_sock, ( struct sockaddr *)&local_addr, server_addr_len ) != 0 )
	{
		printf("Bind ER\n");
                return -1;
	} 
	else {
		printf("Bind OK\n");
	}
	
	while( 1 )
	{
		printf( "Server ceka na data\n" );
		
		client_addr_len = sizeof( remote_addr );
		n = recvfrom(server_sock, c_buff, 256, 0, (struct sockaddr*)&remote_addr, &client_addr_len );
	
		c_buff[n] = 0;			

		printf( "Pripojil se klient\n" );
		printf( "Klient poslal = %s\n", c_buff );

		sleep(5);

		printf( "Server odesila = %c\n", c_buff );
		n = sendto( server_sock, c_buff, 256, 0, (struct sockaddr*)&remote_addr, client_addr_len );

		close( client_sock );

	}
}

