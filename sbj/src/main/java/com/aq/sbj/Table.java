package com.aq.sbj;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;

/**
 * Created by amq102 on 6/5/2015.
 */
public class Table extends Observable{
    private static Table ourInstance = new Table();
    private static final int MAX_SPLITS = 4;
    private final int DEFAULT_BET = 10;

    private int activeHandIndex;
    public List<Bet> Bets;

    public boolean isDealersTurn() {
        return dealersTurn;
    }

    private boolean dealersTurn;

    public static Table getInstance() {
        return ourInstance;
    }

    private Deck deck;
    BankRoll bankRoll;
    public Hand dealer;
    public List<Hand> hands;
    public EnumSet<OP> ops=EnumSet.noneOf(OP.class);
    private Table() {
        bankRoll=new BankRoll(200,"Bank");
        hands = new ArrayList<Hand>(MAX_SPLITS * 2);
        deck = new RandomDeck();
        for (int i = 0; i < MAX_SPLITS * 2; i++) {
            hands.add( new Hand(deck));
        }
        Bets = new ArrayList<Bet>(MAX_SPLITS*2);
        dealer=new Hand(deck);
    }

    public boolean StateNewHand() {
        return ops.equals(EnumSet.of(OP.Hit));
    }

    int lastBetAmount;

    public void PlaceBets(BankRoll bankRoll, int bet) {
        Bets.set(0, new Bet(bet, bankRoll));
        Bets.set(MAX_SPLITS, new Bet(bet, bankRoll));
        lastBetAmount=bet;
    }

    public void doubleDown(Hand hand) {
        Hit(hand);
        Stand(hand);
        hand.setDoubled();
    }

    public void Hit(Hand hand) {
        hand.Deal();
        Evaluate();
    }

    public String CardsString(Hand hand, String separator, boolean obscure)
    {
        StringBuilder sb=new StringBuilder((2+separator.length())* Hand.INIT_HAND_SIZE);
        for (int i = 0; i < hand.size()-1; i++) {
            if (i==1 && obscure){
                sb.append("XX").append(separator);
            }

            else
            {
                sb.append(hand.get(i).toString()).append( separator);
            }
        }
        if (hand.size()==2 && obscure) {
                sb.append("XX");
        } else {
            sb.append(hand.get(hand.size() - 1));
        }
        return sb.toString();
    }

    public String HandToString(Hand hand, boolean obscure) {
        return ((hand.isSoft() && !obscure)? "SOFT":"    ")+ " "+((obscure)?("  "):(String.format("%1$2s",hand.getTotal())))+ " : "+ CardsString(hand," ", obscure);
    }
    public String textDisplay() {
        StringBuilder out = new StringBuilder();
        out.append(bankRoll.toString() + "\n");
        out.append("Dealer: ").append(HandToString(dealer, !isDealersTurn())).append("\n\n");

        for (int i = 0; i < MAX_SPLITS * 2; i++) {
            if (hands.get(i).size() == 0) {
                out.append("\n");
            } else {
                out.append(((i==activeHandIndex)?("***"):("   "))).append(HandToString(hands.get(i),false));
            }
        }
        return out.toString();
    }
    public static void main(String[] params)
    {
        test1();
    }

    private static void test1() {
        Table table=Table.getInstance();

        table.hands.get(0).Deal();

        table.hands.get(Table.MAX_SPLITS).Deal();
        table.dealer.Deal();
        table.hands.get(0).Deal();
        table.hands.get(Table.MAX_SPLITS).Deal();
        table.dealer.Deal();
        System.out.println(table.textDisplay());
    }

    private void Evaluate() {


        this.setChanged();
        this.notifyObservers();
    }

    public void Stand(Hand hand) {
        activateNextHand();
    }

    /**
     * Start dealer hand if hands are done
     * if
     */
    private void activateNextHand() {
        while (true)
        {
            activeHandIndex++;
            if (activeHandIndex >= MAX_SPLITS * 2)
                {
                    StartDealerProcessing();
                    break;
                }
            else if (hands.get(activeHandIndex).size()==0)
            {
                ;
            }
            else break;
            this.setChanged();
            this.notifyObservers();
        }


    }

    private void StartDealerProcessing() {
    }

    public void setDealersTurn(boolean dealersTurn) {
        this.dealersTurn = dealersTurn;
    }
}


