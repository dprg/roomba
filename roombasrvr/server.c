/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

void error(char *msg)
{
    perror(msg);
    exit(1);
}

int wait4client(int portno)
{
     int sockfd, newsockfd, clilen;
     struct sockaddr_in serv_addr, cli_addr;

     sockfd = socket(AF_INET, SOCK_STREAM, 0);
     if (sockfd < 0) 
        error("ERROR opening socket");
     bzero((char *) &serv_addr, sizeof(serv_addr));
     serv_addr.sin_family = AF_INET;
     serv_addr.sin_addr.s_addr = INADDR_ANY;
     serv_addr.sin_port = htons(portno);
     if (bind(sockfd, (struct sockaddr *) &serv_addr,
              sizeof(serv_addr)) < 0) 
              error("ERROR on binding");
     listen(sockfd,5);
     clilen = sizeof(cli_addr);
     newsockfd = accept(sockfd, 
                 (struct sockaddr *) &cli_addr, 
                 &clilen);
     if (newsockfd < 0) 
          error("ERROR on accept");
     printf("client connected on port %d\n", portno);
     return(newsockfd);
}

int echoSocket(int newsockfd)
{
     int n;
     char buffer[100000];

     memset(buffer,0x41, 40000);
     //n = read(newsockfd,buffer,255);
     //if (n < 0) error("ERROR reading from socket");
     //printf("Here is the message: %s\n",buffer);
     n = write(newsockfd,buffer,40000);
     if (n < 0) error("ERROR writing to socket");
     return 0; 
}
