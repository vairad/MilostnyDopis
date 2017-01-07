#include "package.h"

#include <algorithm>

const int GameDeck::size = 16;

short GameDeck::getGivedCount() const
{
    return gived_count;
}

GameDeck::GameDeck() : gived_count(0)
{
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);

    cards.push_back(GameCards::priest);
    cards.push_back(GameCards::priest);

    cards.push_back(GameCards::baron);
    cards.push_back(GameCards::baron);

    cards.push_back(GameCards::komorna);
    cards.push_back(GameCards::komorna);

    cards.push_back(GameCards::prince);
    cards.push_back(GameCards::prince);

    cards.push_back(GameCards::king);

    cards.push_back(GameCards::countess);

    cards.push_back(GameCards::princess);

    std::random_shuffle (cards.begin(), cards.end());
}

GameDeck::~GameDeck(){
    cards.clear();
}

GameCards GameDeck::getNextCard()
{
    GameCards nextCard = cards.front();
    cards.pop_front();
    gived_count++;
    return nextCard;
}

//============== STATICKE METODY TYKAJICI SE KARET =====================================================================


std::string GameDeck::cardToXml(GameCards card)
{
    std::string cardXml = "<card>";
    cardXml += "<type>";
    cardXml += std::to_string(card);
    cardXml += "</type>";

    cardXml += "<name>";
    cardXml += "todo name of card"; //todo name of card
    cardXml += "</name>";
    cardXml += "</card>";
    return cardXml;
}

GameCards GameDeck::getCardByInt(int card)
{
    switch(card){
    case 1:
        return GameCards::guardian;
        break;
    case 2:
        return GameCards::priest;
        break;
    case 3:
        return GameCards::baron;
        break;
    case 4:
        return GameCards::komorna;
        break;
    case 5:
        return GameCards::prince;
        break;
    case 6:
        return GameCards::king;
        break;
    case 7:
        return GameCards::countess;
        break;
    case 8:
        return GameCards::princess;
        break;
    default:
        return GameCards::none;
        break;
    }
}
