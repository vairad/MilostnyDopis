#############################################################################
# Makefile for building: MilostnyDopisServer
#############################################################################

####### Compiler, tools and options

CXX           = g++
CXXFLAGS      = -O2 -std=c++0x -Wall -W -DRELEASE $(DEFINES)
INCPATH       = -I../MilostnyDopisServer -I. 
LINK          = g++
LFLAGS        = -Wl,-O3 -Wl,-rpath, -pthread

####### Output directory

OBJECTS_DIR   = ./

####### Files

SOURCES       = ../MilostnyDopisServer/main.cpp \
		../MilostnyDopisServer/log/log.cpp \
		../MilostnyDopisServer/netservice/netstructure.cpp \
		../MilostnyDopisServer/netservice/receiver.cpp \
		../MilostnyDopisServer/netservice/sender.cpp \
		../MilostnyDopisServer/message/messagequeue.cpp \
		../MilostnyDopisServer/message/message.cpp \
		../MilostnyDopisServer/message/messagehandler.cpp \
		../MilostnyDopisServer/users/user.cpp \
		../MilostnyDopisServer/users/userdatabase.cpp \
		../MilostnyDopisServer/game/game.cpp \
		../MilostnyDopisServer/game/package.cpp \
		../MilostnyDopisServer/game/gameservices.cpp \
		../MilostnyDopisServer/game/player.cpp \
		../MilostnyDopisServer/util/utilities.cpp \
		../MilostnyDopisServer/message/gamehandler.cpp \
		../MilostnyDopisServer/message/loginhandler.cpp 
OBJECTS       = main.o \
		log.o \
		netstructure.o \
		receiver.o \
		sender.o \
		messagequeue.o \
		message.o \
		messagehandler.o \
		user.o \
		userdatabase.o \
		game.o \
		package.o \
		gameservices.o \
		player.o \
		utilities.o \
		gamehandler.o \
		loginhandler.o
DIST          = log/log.h \
		netservice/netstructure.h \
		netservice/receiver.h \
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
		game/player.h \
		util/utilities.h \
		message/gamehandler.h \
		message/loginhandler.h ../MilostnyDopisServer/main.cpp \
		../MilostnyDopisServer/log/log.cpp \
		../MilostnyDopisServer/netservice/netstructure.cpp \
		../MilostnyDopisServer/netservice/receiver.cpp \
		../MilostnyDopisServer/netservice/sender.cpp \
		../MilostnyDopisServer/message/messagequeue.cpp \
		../MilostnyDopisServer/message/message.cpp \
		../MilostnyDopisServer/message/messagehandler.cpp \
		../MilostnyDopisServer/users/user.cpp \
		../MilostnyDopisServer/users/userdatabase.cpp \
		../MilostnyDopisServer/game/game.cpp \
		../MilostnyDopisServer/game/package.cpp \
		../MilostnyDopisServer/game/gameservices.cpp \
		../MilostnyDopisServer/game/player.cpp \
		../MilostnyDopisServer/util/utilities.cpp \
		../MilostnyDopisServer/message/gamehandler.cpp \
		../MilostnyDopisServer/message/loginhandler.cpp
TARGET        = MilostnyDopisServer


first: all
####### Implicit rules

.SUFFIXES: .o .c .cpp .cc .cxx .C

.cpp.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.cc.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.cxx.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.C.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.c.o:
	$(CC) -c $(CFLAGS) $(INCPATH) -o "$@" "$<"

####### Build rules

$(TARGET):  $(OBJECTS)  
	$(LINK) $(LFLAGS) -o $(TARGET) $(OBJECTS) $(OBJCOMP) $(LIBS)

all: $(TARGET)

clean:
	rm -f $(OBJECTS)

cleanAll: clean
	rm -f $(TARGET)
####### Compile

main.o: ../MilostnyDopisServer/main.cpp ../MilostnyDopisServer/errornumber.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/netservice/netstructure.h \
		../MilostnyDopisServer/netservice/receiver.h \
		../MilostnyDopisServer/netservice/optcode.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/netservice/sender.h \
		../MilostnyDopisServer/message/messagehandler.h \
		../MilostnyDopisServer/users/userdatabase.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/util/utilities.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o main.o ../MilostnyDopisServer/main.cpp

log.o: ../MilostnyDopisServer/log/log.cpp ../MilostnyDopisServer/log/log.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o log.o ../MilostnyDopisServer/log/log.cpp

netstructure.o: ../MilostnyDopisServer/netservice/netstructure.cpp ../MilostnyDopisServer/netservice/netstructure.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/errornumber.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o netstructure.o ../MilostnyDopisServer/netservice/netstructure.cpp

receiver.o: ../MilostnyDopisServer/netservice/receiver.cpp ../MilostnyDopisServer/netservice/receiver.h \
		../MilostnyDopisServer/netservice/optcode.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/netservice/netstructure.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/errornumber.h \
		../MilostnyDopisServer/users/userdatabase.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o receiver.o ../MilostnyDopisServer/netservice/receiver.cpp

sender.o: ../MilostnyDopisServer/netservice/sender.cpp ../MilostnyDopisServer/netservice/sender.h \
		../MilostnyDopisServer/netservice/optcode.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/errornumber.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o sender.o ../MilostnyDopisServer/netservice/sender.cpp

messagequeue.o: ../MilostnyDopisServer/message/messagequeue.cpp ../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/log/log.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o messagequeue.o ../MilostnyDopisServer/message/messagequeue.cpp

message.o: ../MilostnyDopisServer/message/message.cpp ../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/log/log.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o message.o ../MilostnyDopisServer/message/message.cpp

messagehandler.o: ../MilostnyDopisServer/message/messagehandler.cpp ../MilostnyDopisServer/message/messagehandler.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/errornumber.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/netservice/netstructure.h \
		../MilostnyDopisServer/message/gamehandler.h \
		../MilostnyDopisServer/message/loginhandler.h \
		../MilostnyDopisServer/util/utilities.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o messagehandler.o ../MilostnyDopisServer/message/messagehandler.cpp

user.o: ../MilostnyDopisServer/users/user.cpp ../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/util/utilities.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o user.o ../MilostnyDopisServer/users/user.cpp

userdatabase.o: ../MilostnyDopisServer/users/userdatabase.cpp ../MilostnyDopisServer/users/userdatabase.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/errornumber.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o userdatabase.o ../MilostnyDopisServer/users/userdatabase.cpp

game.o: ../MilostnyDopisServer/game/game.cpp ../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/message/message.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o game.o ../MilostnyDopisServer/game/game.cpp

package.o: ../MilostnyDopisServer/game/package.cpp ../MilostnyDopisServer/game/package.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o package.o ../MilostnyDopisServer/game/package.cpp

gameservices.o: ../MilostnyDopisServer/game/gameservices.cpp ../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/errornumber.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o gameservices.o ../MilostnyDopisServer/game/gameservices.cpp

player.o: ../MilostnyDopisServer/game/player.cpp ../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/message/messagequeue.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o player.o ../MilostnyDopisServer/game/player.cpp

utilities.o: ../MilostnyDopisServer/util/utilities.cpp ../MilostnyDopisServer/util/utilities.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o utilities.o ../MilostnyDopisServer/util/utilities.cpp

gamehandler.o: ../MilostnyDopisServer/message/gamehandler.cpp ../MilostnyDopisServer/message/gamehandler.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/errornumber.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/users/userdatabase.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/util/utilities.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o gamehandler.o ../MilostnyDopisServer/message/gamehandler.cpp

loginhandler.o: ../MilostnyDopisServer/message/loginhandler.cpp ../MilostnyDopisServer/message/loginhandler.h \
		../MilostnyDopisServer/message/message.h \
		../MilostnyDopisServer/errornumber.h \
		../MilostnyDopisServer/log/log.h \
		../MilostnyDopisServer/message/messagequeue.h \
		../MilostnyDopisServer/users/userdatabase.h \
		../MilostnyDopisServer/users/user.h \
		../MilostnyDopisServer/game/gameservices.h \
		../MilostnyDopisServer/game/game.h \
		../MilostnyDopisServer/game/package.h \
		../MilostnyDopisServer/game/player.h \
		../MilostnyDopisServer/util/utilities.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o loginhandler.o ../MilostnyDopisServer/message/loginhandler.cpp

