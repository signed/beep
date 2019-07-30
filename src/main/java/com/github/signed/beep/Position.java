package com.github.signed.beep;

class Position<T>{
    final int index;
    final T element;

    Position(int index, T element){
        this.index = index;
        this.element = element;
    }
}
