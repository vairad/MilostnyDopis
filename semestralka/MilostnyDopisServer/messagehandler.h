#ifndef MESSAGEHANDLER_H
#define MESSAGEHANDLER_H

#include <pthread.h>

class MessageHandler
{
   int run_flag;
public:
    MessageHandler(int worker_count);

    static pthread_t *handler_threads_p;

    static void initThreads();
    static void *handlerStart(void *service_ptr);
private:

};

#endif // MESSAGEHANDLER_H
