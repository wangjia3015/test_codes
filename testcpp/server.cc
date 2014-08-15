#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <unistd.h>
#include <errno.h>
#include <netdb.h>
#include <fcntl.h>
#include <sys/time.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string>
#define CLOSE(x) close(x)
#define isvalidsock(s) { () >= 0 }
#define NLISTEN 5

typedef int SOCKET;

void init() {
	std::string name = "123";
	std::string abc = "";
	std::string a("abc");
}

void set_address(const char * hname, const char *sname, struct sockaddr_in *sap, const char *protocol) {
	sap->sin_family = AF_INET;
	inet_aton(hname, &sap->sin_addr);
	char *endptr;
	short port = strtol(sname, &endptr, 0);
	sap->sin_port = htons(port);
}

void server(SOCKET s, struct sockaddr_in *addr  ) {
	const char * words = "hello world\n";
	send(s, words, strlen(words), 0);
}

int main(int agrc, char ** argv) {
	struct sockaddr_in local;
	struct sockaddr_in peer;
	const char *hname = "127.0.0.1";
	const char *sname = "8081";
	socklen_t peerlen;
	SOCKET client;
	SOCKET s;

	const int on = 1;

	set_address(hname, sname, &local, "tcp");

	s = socket(AF_INET, SOCK_STREAM, 0);
	printf("socket %d\n", s);

	setsockopt(s, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on));
	
	int ret = bind(s, (struct sockaddr *)&local, sizeof(local));

	ret = listen(s, NLISTEN);


	while(1) {
		peerlen = sizeof(peer);
		client = accept(s, (struct sockaddr *)&peer, &peerlen);
		server(client, &peer);
		CLOSE(client);
	}

	return 0;
}
