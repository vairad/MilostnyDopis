#ifndef GAMEHANDLER_H
#define GAMEHANDLER_H

#include "message/message.h"

class GameHandler
{
public:
    static void handleTypeGame(Message *msg);
private:
    static void handleGameNEW(Message *msg);
    static void handleGameCOD(Message *msg);
    static void handleGameECH(Message *msg);
    static void handleGameSTA(Message *msg);
    static void handleGameTOK(Message *msg);

    static bool checkLogged(int socket);
    static bool checkGame(std::string uid);
};

#endif // GAMEHANDLER_H
