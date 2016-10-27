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
#include "netservice/optcode.h"
#include "errornumber.h"


pthread_t *Reciever::listen_thread_p;
unsigned long Reciever::recieved_bytes = 0;
unsigned long Reciever::recieved_bytes_overflow = 0;

Reciever::Reciever(NetStructure *net):
                            netStructure_p(net)
{
    LOG_INFO("Reciever::Reciever()");
}

void Reciever::recv_bytes(unsigned int byte_count)
{
    unsigned long old_val = recieved_bytes;
    recieved_bytes += byte_count;
    if (old_val < recieved_bytes){
        recieved_bytes_overflow ++;
    }
}

void Reciever::stop()
{
    run_flag = false;
}

void Reciever::initThreads(Reciever *service)
{
   LOG_INFO("Reciever::initThreads()");
   listen_thread_p = (pthread_t *) malloc(sizeof(pthread_t));
   if(listen_thread_p == NULL)
   {
        LOG_ERROR("Vlákno netservice nebylo vytvořeno.");
        MSG("Chyba při startování programu. Ukončuji program.");
        exit(THREAD_MEMORY_ERROR_REC);
   }

    int result = pthread_create(listen_thread_p, NULL, &Reciever::listenerStart, service);
    if(result)
    { // 0 = success
          LOG_ERROR_P1("Vlákno netservice nebylo inicialiováno. chybová hodnota", result);
          MSG("Chyba při startování programu. Ukončuji program.");
          exit(THREAD_CREATION_ERROR_REC);
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

    MSG("Server naslouchá");
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
            MSG("Chyba při akceptování spojení");
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
                    handle_message(fd);
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

void Reciever::handle_message(int socket)
{
    LOG_INFO("Reciever::handle_message()");
    memset (message_buffer, 0, MESSAGE_BUFFER_SIZE);
    int recv_bytes;
    recv_bytes = recv(socket, message_buffer, 7, MSG_PEEK);

    Reciever::recv_bytes(recv_bytes);

    message_buffer[recv_bytes] = 0;

    //check message start
    if(!(message_buffer[0] == '#') && !(message_buffer[1] == '#') && !(message_buffer[2] == '#')){
        recv(socket, message_buffer, 1, 0);
        LOG_DEBUG("Zpráva bez záhlaví byla ignorována");
        return;
    }

    int n = atoi(message_buffer+MSG_LEN_OFFSET);
    if(n < 1){
        LOG_ERROR("Nesymslná zpráva ... délka MENŠÍ než 1 byte");
        n = 1;
    }
    if(n > 2047){
        LOG_ERROR("Nesymslná zpráva ... délka větší než 2047 bytů");
    }

    recv_bytes = recv(socket, message_buffer, n, 0);

    if(recv_bytes > 0)
    {
        message_buffer[recv_bytes] = 0;
        LOG_DEBUG_PS("Obdržel jsem zprávu:", message_buffer);

        create_message(socket);

        return;
    }
    LOG_DEBUG("Prázdná zpráva byla ignorována");
}

void Reciever::create_message(int fd){
    MessageType type;
    Event event;

    char opt[OPT_SIZE + 1];
    memset(opt, 0, OPT_SIZE + 1);
    memcpy(opt, message_buffer + TYPE_OFFSET , OPT_SIZE);
    //opt[OPT_SIZE + 1] = 0;

    type = choose_type(opt);

    memset(opt, 0, OPT_SIZE + 1);
    memcpy(opt, message_buffer + EVENT_OFFSET, OPT_SIZE);
    //opt[OPT_SIZE + 1] = 0;

    event = choose_event(opt);

    if(event == Event::UNK || type == MessageType::unknown){
        LOG_DEBUG("Neznámé OPT kódy. Ignoruji zprávu.");
        return;
    }

    Message *message = new Message(fd, type, event, std::string(message_buffer + CONTENT_OFFSET));
    MessageQueue::recieveInstance()->push_msg(message);
}

MessageType Reciever::choose_type(char *opt){
    if(strcmp(opt, OPT_MSG) == 0){
        return  MessageType::message;
    }else if(strcmp(opt, OPT_GAM) == 0){
        return  MessageType::game;
    }else{
        return MessageType::unknown;
    }
}

Event Reciever::choose_event(char *opt){
    if(strcmp(opt, OPT_ACK) == 0){
        return  Event::ACK;
    }else if(strcmp(opt, OPT_NAK) == 0){
        return  Event::NAK;
    }else if(strcmp(opt, OPT_ECH) == 0){
        return  Event::ECH;
    }else{
        return Event::UNK;
    }
}
