#ifndef PACKAGE_H
#define PACKAGE_H

#include <vector>

enum GameCards{
    guardian    //1 strážná
    , priest    //2 kněz
    , baron     //3 baron
    , komorna   //4 komorná
    , prince    //5 princ
    , king      //6 král
    , countess  //7 hraběnka
    , princess  //8 princezna
};


class GameDeck
{
    std::vector<GameCards> cards;

public:
    GameDeck();
private:
    void shuffleDeck();
};

#endif // PACKAGE_H
