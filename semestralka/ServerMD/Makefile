CC=gcc
OBJ=server.o log.o netservice.o

all:	clean server

server: ${OBJ} 
	${CC} ${OBJ} -o server

server.o: server.c log.h netservice.h
	${CC} -c server.c
log.o: log.c
	${CC} -c log.c
netservice.o: netservice.c
	${CC} -c netservice.c
clean: 
	rm -f server

