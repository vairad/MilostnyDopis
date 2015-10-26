#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include <pthread.h>

#define BUFFSIZE 255
void Die(char *mess) { perror(mess); exit(1); }

struct data_do_vlakna {
	int sock;
	char buffer[BUFFSIZE];
	int received;
	struct sockaddr_in echoclient;
};

void *vlakno_fce(void *v_param) {
	struct data_do_vlakna *param = (struct data_do_vlakna*) v_param;
		
	fprintf(stderr, "Client connected: %s\n", inet_ntoa((*param).echoclient.sin_addr));
	/* Send the message back to client */
	if (sendto((*param).sock, (*param).buffer, (*param).received, 0, (struct sockaddr *) &(*param).echoclient, sizeof((*param).echoclient)) != (*param).received)
		Die("Mismatch in number of echo'd bytes");
}

int main(int argc, char *argv[]) {
	int sock;
	struct sockaddr_in echoserver;
	struct sockaddr_in echoclient;
	char buffer[BUFFSIZE];
	unsigned int echolen, clientlen, serverlen;
	int received = 0;

	pthread_t vlakno;
	struct data_do_vlakna vl_data;
	
	if (argc != 2) {
		fprintf(stderr, "USAGE: %s <port>\n", argv[0]);
		exit(1);
	}
	    
	/* Create the UDP socket */
	if ((sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0)
		Die("Failed to create socket");
	
	/* Construct the server sockaddr_in structure */
	memset(&echoserver, 0, sizeof(echoserver));       /* Clear struct */
	echoserver.sin_family = AF_INET;                  /* Internet/IP */
	echoserver.sin_addr.s_addr = htonl(INADDR_ANY);   /* Any IP address */
	echoserver.sin_port = htons(atoi(argv[1]));       /* server port */

	/* Bind the socket */
	serverlen = sizeof(echoserver);
	if (bind(sock, (struct sockaddr *) &echoserver, serverlen) < 0)
		Die("Failed to bind server socket");
	  
	/* Run until cancelled */
	while (1) {
		/* Receive a message from the client */
		clientlen = sizeof(echoclient);
		if ((received = recvfrom(sock, buffer, BUFFSIZE, 0, (struct sockaddr *) &echoclient, &clientlen)) < 0)
			Die("Failed to receive message");
		
		vl_data.sock = sock;
		vl_data.sock = sock;
		strcpy(vl_data.buffer, buffer);
		vl_data.echoclient = echoclient;
		pthread_create(&vlakno,NULL,vlakno_fce,&vl_data);
	}
}
