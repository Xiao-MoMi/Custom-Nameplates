package net.momirealms.customnameplates.font;


public record FontChar(char left, char middle, char right) {

    public char getLeft() {
        return this.left;
    }

    public char getMiddle() {
        return this.middle;
    }

    public char getRight() {
        return this.right;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FontChar fontInfo)) {
            return false;
        }
        return this.getLeft() == fontInfo.getLeft() && this.getMiddle() == fontInfo.getMiddle() && this.getRight() == fontInfo.getRight();
    }

    @Override
    public int hashCode() {
        return ((59 + this.getLeft()) * 59 + this.getMiddle()) * 59 + this.getRight();
    }

    @Override
    public String toString() {
        return "FontChar=" + this.getLeft() + this.getMiddle() + this.getRight();
    }
}