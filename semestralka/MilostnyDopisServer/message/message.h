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
    COD,
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


    void setMsg(const std::string &value);
    void setEvent(const Event &value);
    void setType(const MessageType &value);
};

#endif // MESSAGE_H
