package com.aq.sbj;

import java.io.IOException;
import java.util.*;

public class Hand  {

    public static final int INIT_HAND_SIZE = 8;

    public Hand(Deck deck) {
        this.deck = deck;
        cardList=new ArrayList<Card>(INIT_HAND_SIZE);
        this.NewHand();

    }

    private void NewHand() {
        //total=0;
        cardList.clear();
        //TODO: should i delegate all cardList Functions? probably, reevatlute how i interact with list, including deal and swap
        //soft=false;
    }

    public Hand() {
        this(new RandomDeck());
    }


    private boolean soft=false;
    private int total;
    private Deck deck;
    private List<Card> cardList;
    public static synchronized void Swap (Hand hand1, Hand hand2)
    {
        hand1.privateDeal(hand2.cardList.get(1));
    }
    public synchronized void privateDeal( Card newCard)
    {
        cardList.add(newCard);
        calcTotal();
    }

    private void calcTotal() {
        total=0;
        soft=false;
        for (int i = 0; i < cardList.size(); i++) {


            if (newCard.getRank()==1)
            {
                if (soft){
                    total++;
                }
                else    {
                    total+=11;
                    soft=true;
                }
            }
            else if (newCard.getRank()>10)
            {

            }
            else //2-9
            {

            }
        }
        if (soft && total>21) {
            total -= 10;
            soft=false;
        }
    }


    public void Deal ()
    {
        privateDeal(deck.getCard());
    }
    public String  HandString(String seperator)
    {
        StringBuilder sb=new StringBuilder((2+seperator.length())* INIT_HAND_SIZE);
        for (int i = 0; i < cardList.size()-1; i++) {
            sb.append(cardList.get(i).toString()+seperator);
        }
        sb.append(cardList.get(cardList.size()-1));
        return sb.toString();
    }
    @Override
    public String toString() {
        return (soft)? "SOFT":"    "+ " "+total + " : "+ HandString(" ");
    }
}
