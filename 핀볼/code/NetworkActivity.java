package com.example.pinball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinball.Engine.Network.NetManager;
import com.example.pinball.Engine.Network.NetPhysicsView;
import com.example.pinball.Engine.PhysicsObject;
import com.example.pinball.Engine.PhysicsView;
import com.example.pinball.GameCharacter.GameCharacter;
import com.example.pinball.GameCharacter.TestCharacter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetworkActivity extends AppCompatActivity{
    private NetManager netManager = new NetManager();
    private boolean isPaceMaker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_make);
    }

}
