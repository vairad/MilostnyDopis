#include "log.h"
#include "netservice.h"


///////////////////////////////////////////////////////////// ARGUMENT CHECK
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


///////////////////////////////////////////////////////////// MAIN
int main(int argc, const char* argv[])
{
	int server_sock;
	int port = 1234;

    if(prepare_socket(port, &server_sock)){

        init_synchronize(); //inicializace semaforuu

        start_netservice(&server_sock);
        return 0;
    }

    return 5;
}

