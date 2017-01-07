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

void Message::setMsg(const std::string &value)
{
    msg = value;
}

void Message::setEvent(const Event &value)
{
    event = value;
}

void Message::setType(const MessageType &value)
{
    type = value;
}

Message::Message(int socket, MessageType type, Event event, std::string msg):
    socket(socket), type(type), event(event), msg(msg)
{
    
}

Message::~Message()
{
}

void Message::print()
{
    MSG(msg.c_str());
}
