#include "log.h"

void test_log_macros(){

 LOG_TRACE("Trace log")

 LOG_TRACE_P1("Trace log with value", 5)

 LOG_INFO("Info log")

 LOG_INFO_P1("Info log with value", 10)

 LOG_DEBUG("Debug log")

 LOG_DEBUG_P1("Debug log with value", 15)

 LOG_WARNING("Warning log")

 LOG_WARNING_P1("Warning log with value", 20)

 LOG_ERROR("ERRROORR")

 LOG_ERROR_P1("Error log with vaue", 25)

 MSG("Obyčejná zpráva")

 MSG_PS("Zprává se string parametrem" , "parametr")

}

FILE *logfile;

void openLogFile(){
#ifdef RELEASE
    if(logfile == NULL){
        logfile = fopen ("serverLog.txt","a");
    }
#endif
}

void closeLogFile(){
    if(logfile != NULL){
        fclose(logfile);
    }
}
