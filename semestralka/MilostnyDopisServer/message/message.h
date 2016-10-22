#ifndef MESSAGE_H
#define MESSAGE_H

#include <string>

enum MessageType{
    servis, game, login
};

enum Event{
    ACK, NAK
};

class Message
{
    MessageType type;
    Event event;
    std::string msg;
public:
    Message(MessageType type, Event event, std::string msg);
    void print();
};

#endif // MESSAGE_H
