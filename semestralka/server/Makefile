CC=gcc
OBJ=server.o log.o

all:	clean server

server: ${OBJ} 
	${CC} ${OBJ} -o server

server.o: server.c log.h
	${CC} -c server.c
log.o: log.c
	${CC} -c log.c
clean: 
	rm -f server

