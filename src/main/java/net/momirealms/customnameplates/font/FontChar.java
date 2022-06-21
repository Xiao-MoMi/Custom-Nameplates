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

    /*
    如果对象的equals方法被重写，那么对象的HashCode方法也尽量重写
    比如：有个A类重写了equals方法，但是没有重写hashCode方法，看输出结果，对象a1和对象a2使用equals方法相等，
    所以，我们在重写了equals方法后，尽量也重写了hashcode方法，通过一定的算法，使他们在equals相等时，也会有相同的hashcode值。
    */
    @Override
    public int hashCode() {
        return ((59 + this.getLeft()) * 59 + this.getMiddle()) * 59 + this.getRight();
    }

    @Override
    public String toString() {
        return "FontChar=" + this.getLeft() + this.getMiddle() + this.getRight();
    }
}