#include "netstructure.h"


#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <errno.h>
#include "log/log.h"


NetStructure::NetStructure(int port):
                                port_number(port)
{
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
        LOG_ERROR("NetStructure::bind_socet() - nelze vytvořit socket")
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

int NetStructure::bind_socet()
{
    LOG_DEBUG("NetStructure::bind_socet()");
    int len_addr = sizeof(struct sockaddr_in);

    server_socket = socket(AF_INET, SOCK_STREAM, 0);

    if (server_socket == -1) {
        check_socket_creation(errno);
        exit(51);
    }

    memset(&my_addr, 0, len_addr);

    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(port_number);
    my_addr.sin_addr.s_addr = INADDR_ANY;

    int return_value = bind(server_socket, (struct sockaddr *) &my_addr, len_addr);
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
