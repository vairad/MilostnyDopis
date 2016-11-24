#ifndef UTILITIES_H
#define UTILITIES_H

#include <string>

class Utilities
{
public:
    static void replaceAll(std::string &str, const std::string &from, const std::string &to);
    static bool readNumber(std::string numberS, int *number);
};

#endif // UTILITIES_H
