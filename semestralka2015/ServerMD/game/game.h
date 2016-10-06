#ifndef GAME_H_ASDFWICEVEVP
#define GAME_H_ASDFWICEVEVP

#define CARDS_COUNT 16
#define CARD_LEN 4

typedef struct DECK{    
    short played[CARDS_COUNT];
    short played_count;
    short remains[CARDS_COUNT];
    short remains_count;
} deck;

typedef struct GAME{
    int is_running;
    int game_id;

    //deck
    struct DECK deck;

    //players
    short p_count; // pocet hrajicich hracu

    short plays; // kdo je na tahu

    char **p_nick; // pole odkazu na jmena hracu
    char *p_card[4]; // karta, ktera lezi pred hracem

} game;

/**
 * Připrav balíček náhodných karet pro kolo
 * @param deck pointer
 * @return err val
 */
int init_deck(deck *deck);

/**
 * Přiděl mi kartu z balicku
 * @param deck pointer
 * @return err val
 */
short get_card(deck *deck);

/**
 * Pridel cislu karty retezec
 * @param number short id karty
 * @return char pointer
 */
char *get_card_str(short number);

#endif
