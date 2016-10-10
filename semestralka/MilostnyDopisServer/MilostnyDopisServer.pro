TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

SOURCES += main.cpp \
    log/log.cpp \
    netservice/netservice.cpp

HEADERS += log/log.h \
    netservice/netservice.h
