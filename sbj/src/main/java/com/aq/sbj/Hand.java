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
        this.clear();

        //soft=false;
    }

    public Hand() {
        this(new RandomDeck());
    }

    public static void main(String[] params)
    {
        Hand hand=new Hand( );
        for (int i = 0; i < 20; i++) {
            hand.clear();
            for (int j = 0; j < 4; j++) {
                hand.Deal();
                System.out.println(hand.toString());
            }
            System.out.println();
        }
    }
    private boolean soft;
    private int total;
    private Deck deck;
    private List<Card> cardList;
    public static synchronized void Swap (Hand hand1, Hand hand2)
    {
        hand1.privateDeal(hand2.get(1));
    }
    public synchronized void privateDeal( Card newCard)
    {
        this.add(newCard);

    }


    public int getTotal() {
        return total;
    }

    private void calcTotal() {
        total=0;
        soft=false;
        for (int i = 0; i < this.size(); i++) {


            int rank = get(i).getRank();
            if (rank ==1)
            {
                if (soft){
                    total++;
                }
                else    {
                    total+=11;
                    soft=true;
                }
            }
            else if (rank >10)
            {
                total+=10;
            }
            else //2-9
            {
                total+=rank;
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
        for (int i = 0; i < this.size()-1; i++) {
            sb.append(this.get(i).toString()).append( seperator);
        }
        sb.append(this.get(this.size() - 1));
        return sb.toString();
    }
    @Override
    public String toString() {
        return ((soft)? "SOFT":"    ")+ " "+total + " : "+ HandString(" ");
    }

    //<editor-fold desc="cardlist delegates">
    public int size() {
        return cardList.size();
    }

    public boolean isEmpty() {
        return cardList.isEmpty();
    }

    public boolean add(Card card) {

        boolean result = cardList.add(card);
        calcTotal();
        return result;
    }

    public void clear() {
        cardList.clear();
        total=0;
    }

    public Card get(int i) {
        return cardList.get(i);
    }

    public Card set(int i, Card card) {
        Card result = cardList.set(i, card);
        calcTotal();
        return result;
    }

    public void add(int i, Card card) {
        cardList.add(i, card);
        calcTotal();
    }

    public Card remove(int i) {
        Card result = cardList.remove(i);
        calcTotal();
        return result;
    }
    //</editor-fold>
}
