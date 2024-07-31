/**
 * Represents the class Main
 * @author Ilyass EL MAAIDLI & Reda TARGAOUI
 * @since 5 october 2023
 */
package com.DataConverter;

import com.DataConverter.View.HomeView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Data Converter");
        frame.setContentPane(new HomeView().getHomePanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

