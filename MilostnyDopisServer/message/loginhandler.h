#ifndef LOGINHANDLER_H
#define LOGINHANDLER_H

#include "message/message.h"

class LoginHandler
{
public:
    static void handleTypeLogin(Message *msg);
private:
    static void handleLoginECH(Message *msg);
    static void handleLoginCOD(Message *msg);
    static void handleLoginOUT(Message *msg);
};

#endif // LOGINHANDLER_H
