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
    NAK,
    ECH
};

class Message
{
    MessageType type;
    Event event;
    std::string msg;
    int socket;
public:
    Message(int socket, MessageType type, Event event, std::string msg);
    void print();
    MessageType getType();
    Event getEvent();
    std::string getMsg();
    int getSocket();
};

#endif // MESSAGE_H
