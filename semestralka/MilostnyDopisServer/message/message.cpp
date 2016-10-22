#include "message.h"

#include "log/log.h"

Message::Message(MessageType type, Event event, std::string msg):
    type(type), event(event), msg(msg)
{

}

void Message::print()
{
    MSG(msg.c_str());
}
