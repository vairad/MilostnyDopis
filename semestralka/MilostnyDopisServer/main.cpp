#include <iostream>
#include "log/log.h"

bool run = false;
int port_number = 2525;

int setup_port(char* portArg){
    int read_port = strtol(portArg, NULL , 10);
    if(read_port > 0 && read_port < 65536){
        port_number = read_port;
        MSG_PD("Server bude spuštěn na portu: ", port_number);
        return 0;
    }else{
        LOG_ERROR_PS("Nezname označení portu: ", portArg);
        MSG_PS("Nezname označení portu: ", portArg);
        return 15;
    }
}


void test(){
    test_log_macros();

}

/**
 * @brief help
 */
void help(){
    MSG("Napoveda programu MilostnyDopis - Server");
    MSG("  -h          ... zobrazí nápovědu pro spuštění");
    MSG("  -r          ... provede spuštění serveru s výchozími parametry")
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
                      LOG_TRACE("Case vychozi beh");
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

int start_server(){
    return 0;
}


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
