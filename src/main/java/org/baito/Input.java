package org.baito;

import org.baito.casino.Casino;

import java.util.Scanner;

public class Input extends Thread {

    Input() {
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
                Casino.onPuddleClose();
                System.exit(0);
            }
        }
    }

}
