#include <stdio.h>

#pragma once
#ifndef LOG_H_123456
#define LOG_H_123456

extern FILE *logfile;
void openLogFile();
void closeLogFile();

#define LOG_TRACE(a)                LOG("TRACE", a)
#define LOG_TRACE_P1(a, num)        LOG_P1("TRACE", a, num)
#define LOG_TRACE_PS(a, num)        LOG_PS("TRACE", a, num)


#define LOG_INFO(a)                 LOG("INFO", a)
#define LOG_INFO_P1(a, num)         LOG_P1("INFO", a, num)
#define LOG_INFO_PS(a, num)         LOG_PS("INFO", a, num)

#define LOG_DEBUG(a)                LOG("DEBUG", a)
#define LOG_DEBUG_P1(a, num)        LOG_P1("DEBUG", a, num)
#define LOG_DEBUG_PS(a, num)        LOG_PS("DEBUG", a, num)

#define LOG_WARNING(a)              LOG("WARN", a)
#define LOG_WARNING_P1(a, num)      LOG_P1("WARN", a, num)
#define LOG_WARNING_PS(a, num)      LOG_PS("WARN", a, num)


#define LOG_ERROR(a)                LOG("ERROR", a)
#define LOG_ERROR_P1(a, num)        LOG_P1("ERROR", a, num)
#define LOG_ERROR_PS(a, s)          LOG_PS("ERROR", a, s)

#define LOG_P1(level, text, decimal)   if(logfile != NULL) fprintf(logfile,"|||||%6s: %s value: %d\n", level, text, decimal); else printf("|||||%6s: %s value: %d\n", level, text, decimal);
#define LOG_PS(level, text, text2)     if(logfile != NULL) fprintf(logfile,"|||||%6s: %s value: %s\n", level, text, text2); else printf("|||||%6s: %s value: %s\n", level, text, text2);

#define LOG(level, text) \
  do { \
    if(logfile == NULL){ \
        openLogFile();  \
    }   \
    if(logfile != NULL){  \
      fprintf(logfile,"|||||%6s: %s\n", level, text); \
    } else {\
      printf("|||||%6s: %s\n", level, text);    \
    }\
  } while (0);


#define MSG(a) printf(">%s\n", a);
#define MSG_PS(a, b) printf(">%s %s\n", a, b);
#define MSG_PD(a, b) printf(">%s %d\n", a, b);
#define MSG_PL(a, b) printf(">%s %ld\n", a, b);

void test_log_macros();

#endif
