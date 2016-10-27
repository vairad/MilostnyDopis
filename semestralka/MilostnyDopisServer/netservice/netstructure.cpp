#include "netstructure.h"

#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <errno.h>

#include "log/log.h"
#include "errornumber.h"

NetStructure::NetStructure(int port, int wait_queue_len):
                                port_number(port)
                              , max_waitng_connections(wait_queue_len)
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
                LOG_ERROR("Systém nepřipoští vytváření dalších socketů");
                break;
            case ENOMEM:
                LOG_ERROR("Nedostatek paměti pro vytvoření socketu");
                break;
            default:
                LOG_ERROR("Neropoznaná příčina");
        }
}

void NetStructure::check_socket_bind(int error_val){
        LOG_ERROR("NetStructure::check_socket_bind() - nelze vytvořit bind")
        switch (error_val){
            case EACCES:
                LOG_ERROR("Socket je vyhrazen superuživateli");
                break;
            case EADDRINUSE:
                LOG_ERROR("Socket je již využíván");
                break;
            default:
                LOG_ERROR("Nefiltrovaná příčina");
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
    my_addr.sin_addr.s_addr = INADDR_ANY;

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

int NetStructure::getPort_number() const
{
    return port_number;
}

void NetStructure::setPort_number(int value)
{
    port_number = value;
}
