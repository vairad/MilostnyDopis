TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

QMAKE_CFLAGS += -std=c99

LIBS += -pthread

SOURCES += main.cpp \
    log/log.cpp \
    netservice/netstructure.cpp \
    netservice/reciever.cpp

HEADERS += log/log.h \
    netservice/netstructure.h \
    netservice/reciever.h
