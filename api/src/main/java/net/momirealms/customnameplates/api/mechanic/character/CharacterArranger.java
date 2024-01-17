package net.momirealms.customnameplates.api.mechanic.character;

public class CharacterArranger {

    public static char currentChar;

    public static void increase() {
        currentChar = (char) (currentChar + '\u0001');
    }

    public static char getAndIncrease() {
        char temp = currentChar;
        increase();
        return temp;
    }

    public static char increaseAndGet() {
        increase();
        return currentChar;
    }

    public static void reset(char c) {
        currentChar = c;
    }
}
