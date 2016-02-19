#include <signal.h>
#include <stdio.h>

#include "test/log.h"
#include "netservice/netservice.h"
#include "game/game.h"
#include "core/service.h"

//////////////////////////TEST DECK
void test_deck(int n){
    int i;
    game g;

    for( ;n > 0; n--){
        init_deck(&g.deck);
        printf("Novy balicek \n");
        for(i = 0; i < 16; ++i){
            printf("karta %d. %s \n", i+1, get_card_str(get_card(&g.deck)));
        }
    }
}


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

void signal_handler(int signo) {
    if(signo == SIGQUIT){
        log_info("Zastavuji server");
        stop_netservice();
        stop_service();
    }
}

void bind_signals(){
    if (signal(SIGQUIT, signal_handler) == SIG_ERR) {
       log_error("Neni mozne zachytit SIGSTOP");
       exit(60);
    }
}


///////////////////////////////////////////////////////////// MAIN
int main(int argc, const char* argv[])
{
	int server_sock;
    int port = 1234;

    if(argc == 2 && argv[1][0]== 't'){
        printf("Testovani balicku \n \n");
        test_deck(6);
        return 0;
    }

    bind_signals();

    if(prepare_socket(port, &server_sock)){

        init_synchronize(); //inicializace semaforuu
        start_netservice(&server_sock); // spusteni komunikacni sluzby

        run_service(); // obsluha příchozích zpráv

        join_netservice(); // cekani na dokonceni komunikacnich sluzeb (korektni ukonceni)
        return 0;
    }

    return 5;
}

