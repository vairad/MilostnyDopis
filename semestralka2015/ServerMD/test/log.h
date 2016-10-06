#ifndef LOG_A458621asfisav
#define LOG_A458621asfisav

#define LEVEL 15

#define LEVEL_TRACE 15
#define LEVEL_INFO 10
#define LEVEL_WARNING 5
#define LEVEL_ERROR 1

void log_error(char *str);
void log_warn(char *str);
void log_info(char *str);
void log_trace(char *str);

void log_msg_out(char *msg);
void log_msg_in(char *msg);

#endif
