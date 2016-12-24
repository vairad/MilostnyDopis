package game;

/**
 * Created by XXXXXXXXXXXXXXXX on 19.12.16.
 */
public enum Card {
      GUARDIAN(1)  //1 strážná
    , PRIEST(2)    //2 kněz
    , BARON(3)     //3 baron
    , KOMORNA(4)   //4 komorná
    , PRINCE(5)    //5 princ
    , KING(6)      //6 král
    , COUNTESS(7)  //7 hraběnka
    , PRINCESS(8)  //8 princezna
    , NONE(9)      // empty card (for priest)
    ;

    private final int value;
    Card(int value) { this.value = value; }
    public int getValue() { return value; }
}
