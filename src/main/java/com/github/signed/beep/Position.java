package com.github.signed.beep;

class Position<T>{
    final int position;
    final T element;

    Position(int position, T element){
        this.position = position;
        this.element = element;
    }
}
