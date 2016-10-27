#include "netstructure.h"

#include <sys/socket.h>
#include <sys/un.h>
#include <sys/types.h>

#include <unistd.h>
#include <stdlib.h>
#include <errno.h>

#include <netinet/in.h>
#include <arpa/inet.h>


#include "log/log.h"
#include "errornumber.h"

NetStructure::NetStructure(int port, int wait_queue_len, char *addr):
                                port_number(port)
                              , max_waitng_connections(wait_queue_len)
                              , c_addr(addr)
{
}

NetStructure::~NetStructure()
{
    LOG_DEBUG("NetStructure::~NetStructure()");
    fd_set to_close = this->sockets_to_serve;
    for( int fd = 3; fd < FD_SETSIZE; fd++ ){
        if( FD_ISSET( fd, &to_close ) ){
            close(fd);
            LOG_TRACE("Soket uzavřen");
        }
    }
}


int NetStructure::getServer_socket() const
{
    return server_socket;
}

void NetStructure::setServer_socket(int value)
{
    server_socket = value;
}

void NetStructure::check_socket_creation(int error_val){
        LOG_ERROR("NetStructure::check_socket_creation() - nelze vytvořit socket")
        switch (error_val){
            case ENFILE:
                MSG("Systém nepřipouští vytváření dalších socketů");
                LOG_ERROR("Systém nepřipouští vytváření dalších socketů");
                break;
            case ENOMEM:
                MSG("Nedostatek paměti pro vytvoření socketu");
                LOG_ERROR("Nedostatek paměti pro vytvoření socketu");
                break;
            default:
                LOG_ERROR_P1("Neropoznaná příčina", error_val);
        }
}

void NetStructure::check_socket_bind(int error_val){
        LOG_ERROR("NetStructure::check_socket_bind() - nelze vytvořit bind")
        switch (error_val){
            case EACCES:
                MSG("Socket je vyhrazen superuživateli");
                LOG_ERROR("Socket je vyhrazen superuživateli");
                break;
            case EADDRINUSE:
                MSG("Socket je již využíván");
                LOG_ERROR("Socket je již využíván");
                break;
            case EADDRNOTAVAIL:
                MSG("Adresa naslouchání není dostupná.");
                LOG_ERROR("Adresa naslouchání není dostupná.");
                break;
            default:
                LOG_ERROR_P1("Nefiltrovaná příčina", error_val);
        }
}

int NetStructure::bind_socket()
{
    LOG_DEBUG("NetStructure::bind_socket()");
    int len_addr = sizeof(struct sockaddr_in);

    server_socket = socket(AF_INET, SOCK_STREAM, 0);

    if (server_socket == -1) {
        check_socket_creation(errno);
        MSG("Chyba při vytváření soketu. Ukončuji program.");
        exit(SOCKET_ERROR);
    }
    LOG_INFO("Socket vytvořen");

    memset(&my_addr, 0, len_addr);

    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(port_number);

    my_addr.sin_addr.s_addr = resolve_address();

    if(my_addr.sin_addr.s_addr == 0){
        MSG_PS("Nesprávný formát adresy", c_addr);
        LOG_ERROR_PS("Neprovedl se překlad adresy do binární podoby", c_addr);
        exit(ADDRESS_ERROR);
    }


    int return_value = bind(server_socket, (struct sockaddr *) &my_addr, len_addr);

    if(return_value == -1){
        check_socket_bind(errno);
        MSG("Chyba při vytváření soketu. Ukončuji program.");
        exit(BIND_ERROR);
    }
    LOG_INFO("Socket navázán");

    return_value = listen(server_socket, max_waitng_connections);

    return return_value;
}

in_addr_t NetStructure::resolve_address(){
    if(strcmp(c_addr, "ALL") == 0){
        return INADDR_ANY;
    }else if(strcmp(c_addr, "localhost") == 0){
         return inet_addr("127.0.0.1");
    }else{
        return inet_addr(c_addr);
    }
}

int NetStructure::getPort_number() const
{
    return port_number;
}

void NetStructure::setPort_number(int value)
{
    port_number = value;
}
