#include <iostream>
#include <csignal>

#include "errornumber.h"
#include "log/log.h"
#include "netservice/netstructure.h"
#include "netservice/reciever.h"
#include "netservice/sender.h"

#include "message/messagequeue.h"
#include "message/messagehandler.h"

//=====================================================================================

bool run = false;
int port_number = 2525;
int wait_que_len = 20;

NetStructure *netStructure;
Reciever *service;

//=====================================================================================
/**
 * Funkce zkontroluje validitu a nastaví číslo portu,
 * na kterém bude server naslouchat
 * @brief setup_port
 * @param portArg
 * @return
 */
int setup_port(char* portArg)
{
    int read_port = strtol(portArg, NULL , 10);
    if(read_port > 0 && read_port < 65536)
    {
        port_number = read_port;
        MSG_PD("Server bude spuštěn na portu: ", port_number)
        if(port_number < 1024){
            MSG("Ke korektnímu spuštění serveru je třeba oprávnění administrátora.")
        }
        return 0;
    }else{
        LOG_ERROR_PS("Nezname označení portu: ", portArg);
        MSG_PS("Nezname označení portu: ", portArg);
        return PORT_RANGE_ERROR;
    }
}

/**
 * Funkce zkontroluje validitu a nastaví číslo z rozsahu integer,
 * @param portArg
 * @return
 */
int setup_int(char* portArg)
{
    int read_int = strtol(portArg, NULL , 10);
    if(read_int > 0 && read_int < INT32_MAX)
    {
        port_number = read_int;
        MSG_PD("Rozpoznáno korektní číslo: ", port_number)
        return 0;
    }else{
        LOG_ERROR_PS("Nezname označení kladného čísla: ", portArg);
        MSG_PS("Nezname označení kladného čísla: ", portArg);
        return INT_RANGE_ERROR;
    }
}

/**
 * Funkce slouží k otestování funkčnosti maker z log.h
 * @brief test
 */
void test()
{
    test_log_macros();
}

/**
 * Funkce vypíše nápovědu na stdout pomocí makra MSG z log.h
 * @brief help
 */
void help(){
    MSG("Napoveda programu MilostnyDopis - Server");
    MSG("  -h          ... zobrazí nápovědu pro spuštění");
    MSG("  -r          ... bude provedeno spuštění serveru")
    MSG("  -p [1-65535]... nastaví port pro naslouchání");
    MSG("  -q [1-...]  ... nastaví délku fronty spojení čekajících na vyřízení");
}

/**
 * Zpracuje hodnoty předané při spuštění serveru.
 *
 * @brief read_args
 * @param argc
 * @param argv
 * @return errval
 */
int read_args(int argc, char** argv)
{
    //zpracovani prepinacu
    for (int i = 1; i < argc; i++)
    {
        if (argv[i][0] == '-' || argv[i][0] == '/') {
            switch (argv[i][1]) {
                case 'h': // zobrazení nápovědy ==================
                case 'H':
                      LOG_TRACE("Case napovedy");
                      help();
                      break;

                case 'r': // běh s výchozími parametry =============
                case 'R':
                      LOG_TRACE("Case spusteni serveru");
                      run = true;
                      break;

                case 'p': // nastavení portu =====================
                case 'P':
                      LOG_TRACE("Case nastaveni portu");
                      if(argc > (i+1))
                      {
                           int result = setup_port(argv[i+1]);
                           if(!result){
                                MSG_PD("Port nebyl nastaven používám výchozí:", port_number);
                           }
                      }
                      break;

            case 'q': // nastavení portu =====================
            case 'Q':
                  LOG_TRACE("Case nastaveni fronty čekajících");
                  if(argc > (i+1))
                  {
                       int result = setup_int(argv[i+1]);
                       if(!result){
                            MSG_PD("Velikost fronty nebyla nastavena používám výchozí:", wait_que_len);
                       }
                  }
                  break;

                default : MSG_PS("Ignoruji neznámý přepínač", argv[i]);
            }
        }
        else {
            //std::cout << "Ignoruji nepovoleny argument" << *argv[i];
        }
    }
    return 0; //success
}


void signal_handler (int sig)
{
    if (sig == SIGINT
            ){
        MSG("Ukončuji server");
        service->stop();
        MessageHandler::stop();
        void *retval;
        MSG("Čekám na ukončení vláken");
        pthread_join(*Reciever::listen_thread_p, &retval);
        MessageHandler::joinThreads();

        delete service;
        delete netStructure;
        MSG("Konec");
        exit(99); // todo clean up and correct and až to bude hotové můžeš sem nastavit nulu :)
    }

}

/**
 * @brief start_server
 * @return
 */
int start_server(){
    netStructure = new NetStructure(port_number, wait_que_len);
    service = new Reciever(netStructure);

    Sender::initialize();
    Sender::startThread();

    MessageHandler::initialize(5);
    MessageHandler::startThreads();

    Reciever::listen_thread_p = (pthread_t *) malloc(sizeof(pthread_t));

    if(Reciever::listen_thread_p == NULL){
        MSG("Nedostatek paměti pro vytvoření serverového vlákna, ukončuji program");
        LOG_ERROR("Nedostatek paměti pro vytvoření Reciever::listen_thread_p")
    }

    pthread_create(Reciever::listen_thread_p, NULL, Reciever::listenerStart, service);

    void *retval;

    pthread_join(*Reciever::listen_thread_p, &retval);
    MessageHandler::joinThreads();
    Sender::joinThread();

    return 0;
}


/**
 * @brief main
 * @param argc
 * @param argv
 * @return
 */
int main(int argc, char *argv[])
{
    int result = 1;

    MSG("Zpracovávám argumenty");
    result = read_args(argc, argv);

    if(result)
    {
        LOG_ERROR_P1("Chyba pri zpracovani argumentu", result);
        return result;
    }

    if(run)
    {
       MSG("Spoustim server");

       if (signal(SIGINT, signal_handler) == SIG_ERR){
           LOG_ERROR("Není možné se navázat na signál");
       }

       result =  start_server();
       LOG_INFO_P1("Navratova hodnota serveru:", result);

    }else{
       LOG_INFO("Server nebyl spuštěn");
       help();
    }


    return result;
}
