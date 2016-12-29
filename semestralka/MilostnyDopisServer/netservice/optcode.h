#ifndef OPTCODE_H
#define OPTCODE_H


#define OPT_SIZE 3

#define TYPE_OFFSET 7
#define EVENT_OFFSET 10

#define CONTENT_OFFSET 14

#define HEADER_CHAR_COUNT 13
#define MESSAGE_HEADER "###"


// Message Type
#define OPT_MSG "MSG"
#define OPT_SRV "SRV"
#define OPT_GAM "GAM"
#define OPT_LOG "LOG"




// Message Event


//#define OPT_UNK "UNK"
#define OPT_ACK "ACK"
#define OPT_NAK "NAK"
#define OPT_ECH "ECH"
#define OPT_COD "COD"
#define OPT_NEW "NEW"
#define OPT_OUT "OUT"
#define OPT_STA "STA"
#define OPT_NEP "NEP" /*new player*/

#endif // OPTCODE_H
