#ifndef NETSTRUCTURE_H
#define NETSTRUCTURE_H

#include <pthread.h>
#include <netinet/in.h>


class NetStructure
{
    int port_number;
    int server_socket;

    struct sockaddr_in my_addr;

public:
    NetStructure(int port);

    int getPort_number() const;
    void setPort_number(int value);

    int getServer_socket() const;
    void setServer_socket(int value);

    int bind_socet();
private:
    void check_socket_creation(int error_val);
};

#endif // NETSTRUCTUREE_H
