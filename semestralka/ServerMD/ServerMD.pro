TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

LIBS += -lpthread

SOURCES +=  server.c \
            log.c \
            netservice.c \
            game.c \
            synchonize.c \
            message_list.c

HEADERS += log.h \
            netservice.h \
            game.h \
            synchonize.h \
            message_list.h
