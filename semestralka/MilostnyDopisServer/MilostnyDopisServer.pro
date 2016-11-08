TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

QMAKE_CFLAGS += -std=c99

LIBS += -pthread

SOURCES += main.cpp \
    log/log.cpp \
    netservice/netstructure.cpp \
    netservice/reciever.cpp \
    netservice/sender.cpp \
    message/messagequeue.cpp \
    message/message.cpp \
    message/messagehandler.cpp \
    users/user.cpp \
    users/userdatabase.cpp \
    game/game.cpp \
    game/package.cpp \
    game/gameservices.cpp \
    game/player.cpp

HEADERS += log/log.h \
    netservice/netstructure.h \
    netservice/reciever.h \
    netservice/sender.h \
    errornumber.h \
    message/messagequeue.h \
    message/message.h \
    message/messagehandler.h \
    netservice/optcode.h \
    users/user.h \
    users/userdatabase.h \
    game/game.h \
    game/package.h \
    game/gameservices.h \
    game/player.h
