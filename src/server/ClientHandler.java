package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private ConsoleServer server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;

    public ClientHandler(ConsoleServer server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {

                    //аутентификация

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth ")) {
                            String[] tokens = str.split(" ");
                            String nick = AuthService.getNicknameByLoginAndPassword(tokens[1], tokens[2]);
//                            if (nick != null){
//                                if (!server.isNickBusy(nick)){
//                                    sendMsg("/auth-OK");
//                                    setNickname(nick);
//                                    server.subscribe(ClientHandler.this);
//                                    break;
//                                } else {
//                                    sendMsg("nickname is busy");
//                                }
//
//                            } else {
//                                sendMsg("wrong login/password");
//                            }
                        }
                    }
//                    while (true) {
//                        String str = in.readUTF();
//                        if (str.startsWith("/") || str.startsWith("@")) {
//                            if ("/end".equals(str)){
//                                out.writeUTF("/server closed");
//                                System.out.printf("Client [%s] disconnected\n", socket.getInetAddress());
//                                break;
//                            } else if (str.startsWith("@")){
//                                String[] tokens = str.split(" ", 2);
//                                server.sendPrivateMsg(this, tokens[0].substring(1, tokens[0].length()), tokens[1]);
//                            }
//                        } else
//                        {
//                            server.broadcastMessage(nickname + ": " + str);
//                        }
//                        System.out.printf("Client [%s] — %s\n", socket.getInetAddress(), str);
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNickname(String nick) {
        this.nickname = nick;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            out.writeUTF("/end");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
