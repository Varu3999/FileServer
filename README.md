# Convex Hull 3D (Incremental algorithum) ![java:v8](https://img.shields.io/badge/Java-v8-brightgreen.svg)

This contains a java implementation of a simple File Server and Client model in which the server can also act as a proxy. Socket library has been used to build the application.

# How to Use

**Download the project in the directory you want**

```sh
$ git clone https://github.com/Varu3999/FileServer
$ cd FileServer
$ javac Client.java
$ javac Server.java
```

**Edit the Input file**
Open instructions.txt using any text editor and give the input in the form:
1) The first line should contain the fileName to be downloaded
2) Then each subsequent lines should contain the IP address and the port of the server in the order of proxy

**Run**

```sh
$ java Client
$ java Server
```

Put the Client.class and Server.class files in the directory where you want to run the client and the server respectively.

# Protocol

1. First, the client makes a socket connection with the first server in the list. If the server is active then the server accepts the connection and a handshake happens between the server and the client by sending "Hi" and "OK" messages but if the server is not available then the connection is dropped and the client stops.
2. Then the client sends the file name to the server and also sends the list of remaining servers.
3. The server checks from the list if there is any server after it, if not it send the data of the file and the socket is closed.
4. But it there are other servers after it then it makes a new socket connection with the next server and a handshake happens between them in the same way. For the next server, this server is just like a client (basic idea of proxy).
5. The same process is repeated until the last node is reached.
6. When the last node is reached then it sends the data of the file to its previous node and the cycle continues till the client and he receives the data.
7. The whole data is not transferred in a single time. Instead, data is transferred in the form of fixed-sized packets. and continuous flow is maintained.

