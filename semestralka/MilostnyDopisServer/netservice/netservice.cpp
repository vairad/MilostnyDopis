#include "netservice.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdlib.h>
#include "log/log.h"

pthread_t *Netservice::listen_thread;

Netservice::Netservice()
{
}

void Netservice::initThreads(Netservice *service)
{
   listen_thread = (pthread_t *) malloc(sizeof(pthread_t));
   if(listen_thread == NULL)
   {
        LOG_ERROR("Vlákno netservice nebylo vytvořeno.");
        exit(61);
   }

    int result = pthread_create(listen_thread, NULL, &Netservice::listenerStart, service);
    if(result)
    { // 0 = success
          LOG_ERROR_P1("Vlákno netservice nebylo inicialiováno. chybová hodnota", result);
          exit(62);
    }
}

void *Netservice::listenerStart(void *service_ptr)
{
    Netservice *service = (Netservice *)service_ptr;

    //bind on port

    //serve incoming messages

    return NULL;
}
