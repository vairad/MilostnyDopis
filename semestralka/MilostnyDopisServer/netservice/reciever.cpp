#include "reciever.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>
#include "log/log.h"

pthread_t *Reciever::listen_thread_p;

Reciever::Reciever(NetStructure *net):
                            netStructure_p(net)
{
}

void Reciever::initThreads(Reciever *service)
{
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
    Reciever *service = (Reciever *)service_ptr;

    //bind on port
    int retVal = service->netStructure_p->bind_socet();


    //serve incoming messages

    return NULL;
}
