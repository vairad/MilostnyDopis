#include "utilities.h"

#include <errno.h>
#include <stdlib.h>

void Utilities::replaceAll(std::string& str, const std::string& from, const std::string& to) {
    if(from.empty())
        return;
    size_t start_pos = 0;
    while((start_pos = str.find(from, start_pos)) != std::string::npos) {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length(); // In case 'to' contains 'from', like replacing 'x' with 'yx'
    }
}

bool Utilities::readNumber(std::string numberS, int *number)
{
   long readed;
   readed = strtol(numberS.c_str(), NULL, 10);
   if(errno == EINVAL || errno == ERANGE){
        return false;
   }
   if(readed > INT32_MAX || readed < INT32_MIN){
       return false;
   }
   (*number) = readed;
   return true;
}
