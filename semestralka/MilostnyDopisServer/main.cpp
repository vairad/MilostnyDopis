#include <iostream>
#include "log/log.h"
#include "netservice/netservice.h"

//=====================================================================================

bool run = false;
int port_number = 2525;

//=====================================================================================
/**
 * Funkce zkontroluje validitu a nastaví číslo portu,
 * na kterém bude server naslouchat
 * @brief setup_port
 * @param portArg
 * @return
 */
int setup_port(char* portArg){
    int read_port = strtol(portArg, NULL , 10);
    if(read_port > 0 && read_port < 65536){
        port_number = read_port;
        MSG_PD("Server bude spuštěn na portu: ", port_number)
        if(port_number < 1024){
            MSG("Ke korektnímu spuštění serveru je třeba oprávnění administrátora.")
        }
        return 0;
    }else{
        LOG_ERROR_PS("Nezname označení portu: ", portArg);
        MSG_PS("Nezname označení portu: ", portArg);
        return 15;
    }
}

/**
 * Funkce slouží k otestování funkčnosti maker z log.h
 * @brief test
 */
void test(){
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
    for (int i = 1; i < argc; i++) {
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
                                MSG("Port nebyl nastaven");
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


/**
 * @brief start_server
 * @return
 */
int start_server(){
    Netservice service();
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
       result =  start_server();
       LOG_INFO_P1("Navratova hodnota serveru:", result);

    }else{
       LOG_INFO("Server nebyl spuštěn");
       help();
    }


    return result;
}
