TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

LIBS += -lpthread

SOURCES +=  server.c \
            test/log.c \
            netservice/netservice.c \
            game/game.c \
            core/synchonize.c \
            netservice/message_list.c

HEADERS += test/log.h \
            netservice/netservice.h \
            game/game.h \
            core/synchonize.h \
            netservice/message_list.h
