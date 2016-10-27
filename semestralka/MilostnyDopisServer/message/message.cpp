#include "message.h"

#include "log/log.h"

MessageType Message::getType()
{
    return type;
}

Event Message::getEvent()
{
    return event;
}

std::string Message::getMsg()
{
    return msg;
}

int Message::getSocket()
{
    return socket;
}

Message::Message(int socket, MessageType type, Event event, std::string msg):
    socket(socket), type(type), event(event), msg(msg)
{
    
}

void Message::print()
{
    MSG(msg.c_str());
}
