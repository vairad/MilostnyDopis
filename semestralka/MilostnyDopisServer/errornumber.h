#ifndef ERRORNUMBER_H
#define ERRORNUMBER_H

//MAIN

#define PORT_RANGE_ERROR 11
#define INT_RANGE_ERROR 12

//NETSERVICE
#define SOCKET_ERROR 21
#define BIND_ERROR 22
#define ADDRESS_ERROR 23
//RECEIVER

#define SELECT_REPEAT_ERROR 31
#define THREAD_MEMORY_ERROR_REC 32
#define THREAD_CREATION_ERROR_REC 33

//MESSAGEHANDLER
#define THREAD_MEMORY_ERROR_MSG 41
#define THREAD_CREATION_ERROR_MSG 42

//SENDER
#define THREAD_MEMORY_ERROR_SEN 51
#define THREAD_CREATION_ERROR_SEN 52
#define NOT_ENOUGH_MEMORY_SEN 53
#define TYPE_NOT_IMPLEMENTED_SEN 54
#define EVENT_NOT_IMPLEMENTED_SEN 55

//GAMESERVICES
#define MUTEX_NOT_INIT_GAMS 61

//USERDATABASE
#define MUTEX_NOT_INIT_USRS 71

#endif // ERRORNUMBER_H
