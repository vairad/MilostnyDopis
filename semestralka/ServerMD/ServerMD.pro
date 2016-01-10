TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

LIBS += -lpthread

SOURCES +=  server.cpp \
            log.cpp \
            netservice.cpp \
            game.cpp \
            synchonize.cpp \
            message_list.cpp

HEADERS += log.h \
            netservice.h \
            game.h \
            synchonize.h \
            message_list.h
