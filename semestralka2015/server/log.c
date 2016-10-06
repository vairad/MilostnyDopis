#include <stdio.h>


void log_error(char *str){
	printf("ERROR  : %s\n", str);
}

void log_warn(char *str){
	printf("WARN   : %s\n", str);
}

void log_info(char *str){
	printf("INFO   : %s\n", str);
}

void log_msg_out(char *msg){
	printf("MSG OUT: %s\n", msg);
}
void log_msg_in(char *msg){
	printf("MSG IN : %s\n", msg);
}
