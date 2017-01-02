#include "package.h"

#include <algorithm>

const int GameDeck::size = 16;

GameDeck::GameDeck()
{
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);
    cards.push_back(GameCards::guardian);

    cards.push_back(GameCards::priest);
    cards.push_back(GameCards::priest);

    cards.push_back(GameCards::baron);
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

GameCards GameDeck::getNextCard()
{
    GameCards nextCard = cards.front();
    cards.pop_front();
    return nextCard;
}

std::string GameDeck::cardToXml(GameCards card)
{
    std::string cardXml = "<card>";
    cardXml += "<type>";
    cardXml += std::to_string(card);
    cardXml += "</type>";

    cardXml += "<name>";
    cardXml += "todo name of card"; //todo name of card
    cardXml += "</name>";
    return cardXml;
}
