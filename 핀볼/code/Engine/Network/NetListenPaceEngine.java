package com.example.pinball.Engine.Network;

import android.os.Handler;
import android.util.Log;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

public class NetListenPaceEngine extends NetListenEngine {

    NetListenPaceEngine(NetPhysicsView netView, ObjectInputStream reader, NetInputSet netInputSet, Handler pushHandler) {
        super(netView, reader, netInputSet, pushHandler);
    }

    @Override
    void listen() {
        LinkedHashMap listenData = getListenData();
        if (listenData != null) {
                Inputs.mergePacing(listenData);
            Log.d("페이스메이커", "데이터 수신");
        }
    }

}
