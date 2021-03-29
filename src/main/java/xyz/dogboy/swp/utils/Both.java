package xyz.dogboy.swp.utils;

public class Both<L,R>{
    L left;
    R right;
    public Both(L left, R right) {
        this.left = left;
        this.right = right;
    }
    public L left() {
        return left;
    }
    public R right() {
        return right;
    }
}