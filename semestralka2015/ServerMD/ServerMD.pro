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
    core/service.c \
    netservice/messages.c

HEADERS += test/log.h \
            netservice/netservice.h \
            game/game.h \
            core/synchonize.h \
    core/service.h \
    netservice/messages.h
