package com.example.pinball.Engine.Network;

import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;

public class NetManager {
    Socket Conn = null;
    public ObjectInputStream Reader;
    public ObjectOutputStream Writer;
    int EnemyCharId =0;

    public String getMyIp() {
        return MyIp;
    } public void setMyIp(String myIp) {
        MyIp = myIp;
    } public String getOtherIP() {
        return OtherIP;
    } public void setOtherIP(String otherIP) {
        OtherIP = otherIP;
    }
    String MyIp ="";
    String OtherIP = "";

    public int getEnemyCharId(){
        return EnemyCharId;
    }

    public void makerServer() {
        new Thread(){
            @Override
            public void run(){
                try {
                    ServerSocket serverSocket = new ServerSocket(37000);
                    Conn = serverSocket.accept();
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                MyIp = Conn.getLocalAddress().toString();
                                OtherIP = Conn.getInetAddress().toString();
                                Writer = new ObjectOutputStream(Conn.getOutputStream());
                                Reader = new ObjectInputStream(Conn.getInputStream());
                                sendServerIp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        private void sendServerIp() {
                            String[] ips = new String[2];
                            ips[0] = MyIp;
                            ips[1] = OtherIP;
                            try {
                                Writer.writeObject(ips);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void excessServer(String ip) {
        final String ipString = ip;
        new Thread(){
            @Override
            public void run(){
                try {
                    Conn = new Socket(ipString,37000);
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Writer = new ObjectOutputStream(Conn.getOutputStream());
                                Reader = new ObjectInputStream(Conn.getInputStream());
                                listenIp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        private void listenIp() {
                            String[] ips = new String[2];
                            try {
                                ips = (String[]) Reader.readObject();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            OtherIP = ips[0];
                            MyIp = ips[1];
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void sendMyData(final int charId) {
        new Thread(){
            @Override
            public void run() {
                try {
                    LinkedHashMap charData = new LinkedHashMap();
                    charData.put("Char", charId);
                    Writer.writeObject(charData);
                    ReadOtherData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void ReadOtherData() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            LinkedHashMap a = (LinkedHashMap) Reader.readObject();
                            EnemyCharId = (int) a.get("Char");
                            OtherIP = Conn.getInetAddress().toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }.start();
    }



    public Socket getSocket(){
        return Conn;
    }
}
