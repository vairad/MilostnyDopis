#include <string.h>
#include <stdlib.h>

#include "game.h"

/* Arrange the N elements of ARRAY in random order.
   Only effective if N is much smaller than RAND_MAX;
   if this may not be the case, use a better random
   number generator. */
void shuffle(short *array, size_t n)
{
    if (n > 1)
    {
        size_t i;
        for (i = 0; i < n - 1; i++)
        {
          size_t j = i + rand() / (RAND_MAX / (n - i) + 1);
          short t = array[j];
          array[j] = array[i];
          array[i] = t;
        }
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////

const char cards[CARDS_COUNT][CARD_LEN] = {"PRC", "HRA", "KRA", "PRI", "PRI", "KOM", "KOM", "BAR",
                        "BAR", "KNE", "KNE", "STR", "STR", "STR", "STR", "STR"};

//////////////////////////////////////////////////////////////////////////////////////////////////////////
int init_deck(deck *deck){
    int i;
    for(i = 0; i < CARDS_COUNT; ++i){
       deck->remains[i] = i;
       deck->played[i] = 0;
    }
    shuffle(deck->remains, CARDS_COUNT);

    deck->played_count = 0;
    deck->remains_count = 16;

    return 1;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
short get_card(deck *deck){
    short card = deck->remains[deck->remains_count - 1];
    deck->remains_count--;
    deck->played[deck->played_count] = card;
    deck->played_count++;
    return card;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
char *get_card_str(short number){
    return cards[number];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////


