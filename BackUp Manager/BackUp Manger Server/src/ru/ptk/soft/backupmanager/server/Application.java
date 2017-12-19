package ru.ptk.soft.backupmanager.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.in;
import java.util.ResourceBundle;

public class Application {

    public static void main(String[] args) {
        
        ResourceBundle boundle = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.server.resource.settings");

        Recipient recipient = new Recipient(boundle.getString("backupPath"), Integer.parseInt(boundle.getString("packetSize")));
        recipient.start();
        
        System.out.println(boundle.getString("serverRunning"));
        System.out.println(boundle.getString("qToQuit"));
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String s;
            while (true) {
                s = reader.readLine();
                if (s.equals("q")) {
                    break;
                }
            }
            System.out.println(boundle.getString("serverStop"));            
        } catch (IOException ex) {
            System.err.println("IO exception!");
        } finally {
            recipient.interrupt();
        }

    }

}
