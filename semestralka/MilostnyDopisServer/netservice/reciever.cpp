#include "reciever.h"

#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <errno.h>


#include "log/log.h"
#include "errornumber.h"


pthread_t *Reciever::listen_thread_p;

Reciever::Reciever(NetStructure *net):
                            netStructure_p(net)
{
    LOG_INFO("Reciever::Reciever()");
}

void Reciever::initThreads(Reciever *service)
{
   LOG_INFO("Reciever::initThreads()");
   listen_thread_p = (pthread_t *) malloc(sizeof(pthread_t));
   if(listen_thread_p == NULL)
   {
        LOG_ERROR("Vlákno netservice nebylo vytvořeno.");
        exit(61);
   }

    int result = pthread_create(listen_thread_p, NULL, &Reciever::listenerStart, service);
    if(result)
    { // 0 = success
          LOG_ERROR_P1("Vlákno netservice nebylo inicialiováno. chybová hodnota", result);
          exit(62);
    }
}

void *Reciever::listenerStart(void *service_ptr)
{
    LOG_INFO("Reciever::listenerStart()");
    Reciever *service = (Reciever *)service_ptr;

    NetStructure *netS_p = service->netStructure_p;


    //bind on port
    int retVal = netS_p->bind_socket();

    if(retVal == -1){
        MSG("Nepodařilo se otevřít příchozí socket, ukončuji program");
        LOG_ERROR("Nepodařilo se naslouchat na socketu listen()");
        return NULL;
    }

    //prepare FD set
    FD_ZERO( &(netS_p->sockets_to_serve) );
    FD_SET( netS_p->getServer_socket(), &(netS_p->sockets_to_serve) );

    //serve incoming messages
    service->serve_messages();

    return NULL;
}

void Reciever::serve_messages()
{
    LOG_INFO("Reciever::serve_messages()");
    error_counter = 0;
    run_flag = true;
    while( run_flag ){
        fd_set tmp_sockets = netStructure_p->sockets_to_serve;
        //select( velikost množiny , změna ke čtení, změna pro zápis, změna výjimka, timeout (NULL = infinity));
        int return_value = select( FD_SETSIZE, &tmp_sockets, NULL, NULL, NULL);
        if(return_value == -1){
            MSG("Chyba při akceptvání spojení");
            check_select_error(errno);
            if(error_counter == REPEATED_ERRORS_LIMIT){
                MSG("Opakovaná chyba při akceptování spojení, nelze se zotavit ukončuji program");
                exit(SELECT_REPEAT_ERROR);
            }
            error_counter++;
            continue;
        }

        //handle changed sockets
        handle_socket_activity(&tmp_sockets);
    }
}

void Reciever::check_select_error(int error_val)
{
    LOG_ERROR("NetStructure::check_select_error() - nelze vytvořit socket")
    switch (error_val){
        case ENOMEM:
            LOG_ERROR("Nedostatek paměti pro funkci select");
            break;
        case EBADF:
            LOG_ERROR("V prohledávané množině je neplatný FD");
            break;
        default:
            LOG_ERROR("Neropoznaná příčina");

    }
}

void Reciever::handle_socket_activity(fd_set* sockets)
{
    struct sockaddr_in peer_address;
    socklen_t address_length = sizeof (peer_address);
    LOG_INFO("Reciever::handle_socket_activity()");
    for( int fd = 3; fd < FD_SETSIZE; fd++ ){
        if( FD_ISSET( fd, sockets ) ){
            if (fd == netStructure_p->getServer_socket()){
                LOG_INFO("Handle incoming connection");
                int client_socket = accept(netStructure_p->getServer_socket()
                                       , (struct sockaddr *) &peer_address
                                       , &address_length);
                FD_SET( client_socket, &(netStructure_p->sockets_to_serve) );
                MSG("Bylo otevřeno nové spojení s klientem");
                return;
            }
            else {
                int bytes_to_read;
                // pocet bajtu co je pripraveno ke cteni
                ioctl( fd, FIONREAD, &bytes_to_read );
                // mame co cist
               if (bytes_to_read > 0){
                    handle_message();
                }
                // na socketu se stalo neco spatneho
                else {
                    close(fd);
                    LOG_TRACE("Socket uzavřen");
                    FD_CLR( fd, &(netStructure_p->sockets_to_serve) );
                    LOG_TRACE("Socket odstraněn ze skupiny");
                    MSG("Bylo uzavřeno spojení s klientem");
                }
            }
        }
    }
}

void Reciever::handle_message()
{
    LOG_INFO("Reciever::handle_message()");
}
