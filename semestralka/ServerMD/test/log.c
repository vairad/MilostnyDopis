#include <stdio.h>


#include "test/log.h"

void log_error(char *str){
    if(LEVEL >= LEVEL_ERROR){
        printf("ERROR  : %s\n", str);
    }
}

void log_warn(char *str){
    if(LEVEL >= LEVEL_WARNING){
        printf("WARN   : %s\n", str);
    }
}

void log_info(char *str){
    if(LEVEL >= LEVEL_INFO){
        printf("INFO   : %s\n", str);
    }
}

void log_trace(char *str){
    if(LEVEL >= LEVEL_TRACE){
        printf("TRACE   : %s\n", str);
    }
}

void log_msg_out(char *msg){
    if(LEVEL >= LEVEL_INFO){
        printf("MSG OUT: %s\n", msg);
    }
}
void log_msg_in(char *msg){
    if(LEVEL >= LEVEL_INFO){
        printf("MSG IN : %s\n", msg);
    }
}
