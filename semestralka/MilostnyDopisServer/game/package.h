#ifndef PACKAGE_H
#define PACKAGE_H

#include <deque>

enum GameCards{
    guardian = 1    //1 strážná
    , priest    //2 kněz
    , baron     //3 baron
    , komorna   //4 komorná
    , prince    //5 princ
    , king      //6 král
    , countess  //7 hraběnka
    , princess  //8 princezna
    , none      // empty card (for priest)
};


class GameDeck
{
    std::deque<GameCards> cards;

public:
    GameDeck();
    static const int size;
    GameCards getNextCard();
private:
    void shuffleDeck();
};

#endif // PACKAGE_H
