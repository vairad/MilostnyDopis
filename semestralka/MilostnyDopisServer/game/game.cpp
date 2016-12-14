#include "game.h"

#include "log/log.h"

std::string Game::getUid() const
{
    return uid;
}

Game::Game(std::string uid, int round_count) : uid(uid), round_count(round_count)
{
}

bool Game::addPlayer(User *who)
{
    if(player1 == NULL){
        player1 = new Player(who);
        player_count++;
        return true;
    }
    if(player2 == NULL){
        player2 = new Player(who);
        player_count++;
        return true;
    }
    if(player3 == NULL){
        player3 = new Player(who);
        player_count++;
        return true;
    }
    if(player4 == NULL){
        player4 = new Player(who);
        player_count++;
        return true;
    }

    if(player1->getUser() == who
            || player2->getUser() == who
            || player3->getUser() == who
            || player4->getUser() == who){
       return true;
    }
    return false;
}

//=================================================================================================

/**
 * Urči hráče a jeho kartu, pokud zvolíš správně hráč je vyřazen ze hry.
 * Nelze zvolit strážnou a nelze zvolit hráče který je chtáněný komornou
 * @brief Game::effectGuardian
 * @param who
 * @param whom
 * @param tip
 * @return
 */
bool Game::effectGuardian(Player *who, Player *whom, GameCards tip)
{
    if(whom->isGuarded()){
        return false;
    }
    if(whom->compareCard(tip)){
        whom->cardOnDesk();
        whom->setInGame(false);
        return true;
    }
    return false;
}

/**
 * Podívej se na kartu zvoleného hráče, pokud není chráněn komornou.
 * @brief Game::effectPriest
 * @param who
 * @param whom
 * @return
 */
GameCards Game::effectPriest(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return GameCards::none;
    }
    return whom->showCard();
}

/**
 * Porovnej si karu se zvoleným hráčem, pokud není chráněn komornou.
 *
 * @brief Game::effectBaron
 * @param who
 * @param whom
 * @return
 */
bool Game::effectBaron(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return false;
    }
    return whom->compareCard(who->showCard());
}

/**
 * Po celé další kolo jsi chráněn před efekty karet ostatních
 * @brief Game::effectMaid
 * @param who
 * @return
 */
bool Game::effectMaid(Player *who)
{
    who->setGuarded(true);
    return true;
}

/**
 * Vylož kartu na stůl a pokud můžeš pokračovat vezmi si další.
 * @brief Game::effectPrince
 * @param who
 * @param whom
 * @return
 */
bool Game::effectPrince(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return false;
    }
    whom->cardOnDesk();
    if(whom->isInGame()){
        giveCard(whom);
    }
    return true;
}

/**
 * Vyměň si kartu s označeným hráčem, pokud není chráněn komornou.
 * @brief Game::effectKing
 * @param who
 * @param whom
 * @return
 */
bool Game::effectKing(Player *who, Player *whom)
{
    if(whom->isGuarded()){
        return false;
    }
    GameCards tmpCard = whom->showCard();
    whom->giveCard(who->showCard());
    who->giveCard(tmpCard);
    return true;
}

/**
 * Vylož kartu, pokud se ti sejde na ruce hraběnka s králem nebo princem.
 * Nemá speciální efekt po odehrání.
 * @brief Game::effectCountess
 * @param who
 * @return
 */
bool Game::effectCountess(Player *who)
{
    // no special efect when card is played
}


/**
 * Pokud zahraješ tuto kartu, hra pro tebe končí.
 * @brief Game::effectPrincess
 * @param who
 * @return
 */
bool Game::effectPrincess(Player *who)
{
    who->setInGame(false);
    return true;
}

/**
 * @brief Game::giveCard
 * @param who
 * @return
 */
bool Game::giveCard(Player *who)
{
    who->setSecondCard(game_deck.getNextCard());
    return true;
}

/** *************************************************************************
 * Vrací aktuální stav hry pro možné zařazení uživatele do hry po výpadku atp
 * @brief Game::getStatus
 * @return
 */
std::string Game::getStatus()
{
    return "soinovnqwpoasnvpseonewvpo";
}

/**
 * @brief Game::toString
 * @return
 */
std::string Game::toString()
{
    std::string tmp;
    tmp += uid;
    tmp += "&&";
    tmp += std::to_string(player_count);
    return tmp;
}



