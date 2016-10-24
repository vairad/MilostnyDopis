#ifndef MESSAGE_H
#define MESSAGE_H

#include <string>

enum MessageType{
    unknown,
    servis,
    game,
    login,
    message
};

enum Event{
    UNK,
    ACK,
    NAK
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
