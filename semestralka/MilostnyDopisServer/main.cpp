#include <iostream>
#include <csignal>
#include <cstring>

#include "errornumber.h"
#include "log/log.h"
#include "netservice/netstructure.h"
#include "netservice/receiver.h"
#include "netservice/sender.h"

#include "message/messagequeue.h"
#include "message/messagehandler.h"

#include "users/userdatabase.h"

#include "game/gameservices.h"

#include "util/utilities.h"
//=====================================================================================

bool run = false;
int port_number = 2525;
int wait_que_len = 20;
char ADDR_ALL[] = {"ALL"};
char *addr = ADDR_ALL;

NetStructure *netStructure;
Receiver *receiver;

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
int setup_que_len(char* portArg)
{
    int read_int = strtol(portArg, NULL , 10);
    if(read_int > 0 && read_int < INT32_MAX)
    {
        wait_que_len = read_int;
        MSG_PD("Délku fronty čekajících připojení bude: ", wait_que_len)
        return 0;
    }else{
        LOG_ERROR_PS("Neznámé označení kladného čísla: ", portArg);
        MSG_PS("Neznámé označení kladného čísla: ", portArg);
        return INT_RANGE_ERROR;
    }
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
 * Funkce vypíše nápovědu příkazů pro server na stdout pomocí makra MSG z log.h
 * @brief help
 */
void helpProgram(){
    MSG("Napoveda příkazů");
    MSG("  help   ");
    MSG("  games  ")
    MSG("  users  ");
    MSG("  konec  ");
}

/**
 * @brief printStats
 */
void printStats(){
    MSG("Statistiky běhu serveru");
    MSG_PL("Bylo odesláno [byte]:", Sender::getSendedBytes());
    MSG_PL("Bylo přijato [byte]:", Receiver::getReceivedBytes());
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
                           if(result != 0){
                                MSG_PD("Port nebyl nastaven používám výchozí:", port_number);
                           }
                      }
                      break;

            case 'a': // nastavení portu =====================
            case 'A':
                  LOG_TRACE("Case nastaveni adresy naslouchani");
                  if(argc > (i+1))
                  {
                       addr = argv[i+1];
                       if(strcmp(addr, "ALL") != 0){
                            MSG_PS("Server bude naslouchat na adrese:", addr);
                       }
                  }
                  break;

            case 'q': // nastavení portu =====================
            case 'Q':
                  LOG_TRACE("Case nastaveni fronty čekajících");
                  if(argc > (i+1))
                  {
                       int result = setup_que_len(argv[i+1]);
                       if(result != 0){
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


//===========================================================================================================================
//===========================================================================================================================
//===========================================================================================================================
// ^^ funkce pro zpracování argumentu
// vv funkce pro server
//===========================================================================================================================
//===========================================================================================================================
//===========================================================================================================================

void stopServer(void){
    MSG("Ukončuji server");
    receiver->stop(); // Receive thread
    Sender::stop(); // Send thread
    MessageHandler::stop(); // All message handler threads
}

void joinThreads(void){

    void *retval;
    pthread_join(*Receiver::listen_thread_p, &retval);
    MessageHandler::joinThreads();
    Sender::joinThread();
}

void freeMemory(){
    delete receiver;
    delete netStructure;
}

void signal_handler (int sig)
{
    if (sig == SIGINT){
        stopServer();
        joinThreads();
        freeMemory();

        MSG("Konec (zpracovaný signál)");
        exit(99); // todo clean up and correct and až to bude hotové můžeš sem nastavit nulu :)
    }

}

void printUsers(){
    std::cout.flush();
    std::cout << std::endl;
    std::cout << "Registrovaní uživatelé" << std::endl;
    for(auto iterator = UserDatabase::getInstance()->begin();
            iterator != UserDatabase::getInstance()->end();
        iterator++)
    {
        std::cout << "user: ";
        std::cout << *((User *)iterator->second)->getNickname();
        std::cout << " id: ";
        std::cout << iterator->first;
        std::cout << std::endl;
    }
    std::cout << std::endl;
    std::cout << ">";
    std::cout.flush();
}

/** ********************************************************************************
 * @brief printGames
 */
void printGames(){
    std::cout << std::endl;
    std::cout << "Aktivní hry:" << std::endl;

    std::string games = GameServices::getInst()->listGames();
    Utilities::replaceAll(games, ";", "\n");
    std::cout << games;
    std::cout << std::endl;
}


/** ********************************************************************************
 * @brief handleCommand
 * @param command
 */
void handleCommand(std::string command){
    if(command.compare("users") == 0){
        printUsers();
        return;
    }
    if(command.compare("games")  == 0){
        printGames();
        return;
    }
    if(command.compare("help")  == 0){
        helpProgram();
        return;
    }
    if(command.compare("konec")  == 0){
        return; // reakce je implementována nad smyčkou čtení příkazů
    }
    MSG("Neznámý příkaz");
}

/** ********************************************************************************
 * Funkce nastartuje server a v nekončné smyčce čte argumenty ze STDIN
 * @brief start_server
 * @return
 */
int start_server(){
    netStructure = new NetStructure(port_number, wait_que_len, addr);
    receiver = new Receiver(netStructure);

    Sender::initialize();
    Sender::startThread();

    MessageHandler::initialize(5);
    MessageHandler::startThreads();

    Receiver::listen_thread_p = (pthread_t *) malloc(sizeof(pthread_t));

    if(Receiver::listen_thread_p == NULL){
        MSG("Nedostatek paměti pro vytvoření serverového vlákna, ukončuji program");
        LOG_ERROR("Nedostatek paměti pro vytvoření Receiver::listen_thread_p")
    }

    pthread_create(Receiver::listen_thread_p, NULL, Receiver::listenerStart, receiver);

    std::string command;

    while( command.compare("konec") != 0 ){
        std::getline(std::cin, command);
        std::cout << "> " << command;
        handleCommand(command);
        std::cout << std::endl;
        std::cout << ">";
    }

    stopServer();
    joinThreads();

    printStats();

    freeMemory();

    MSG("Konec serveru");
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

    MSG("Konec programu");
    return result;
}
