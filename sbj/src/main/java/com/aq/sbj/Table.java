package com.aq.sbj;

import java.util.EnumSet;
import java.util.Observable;

/**
 * Created by amq102 on 6/5/2015.
 */
public class Table extends Observable{
    private static Table ourInstance = new Table();
    public static final int MAX_SPLITS = 4;
    public static final int NO_OF_HANDS = MAX_SPLITS * 2;
    private final int DEFAULT_BET = 10;

    public int activeHandIndex;
    public Bet[] bets;
    public OpFlags ops;
    public boolean isDealersTurn() {
        return dealersTurn;
    }

    private boolean dealersTurn;

    public static Table getInstance() {
        return ourInstance;
    }

    private Deck deck;
    public BankRoll bankRoll;
    public Hand dealer;
    public Hand[] hands;

    private Table() {
        bankRoll=new BankRoll(200,"Bank");
        hands = new Hand[NO_OF_HANDS];
        deck = new RandomDeck();
        for (int i = 0; i < NO_OF_HANDS; i++) {
            hands[i]=( new Hand(deck));
        }
        bets = new Bet[NO_OF_HANDS];
        dealer=new Hand(deck);
        ops=new OpFlags();
        ops.set(OpFlags.NEW_HAND);
    }



    int lastBetAmount;

    public void startGame(BankRoll bankRoll, int bet) {
        for (int i = 0; i < NO_OF_HANDS; i++) {
            hands[i].newHand();
        }
        dealer.newHand();
        setDealersTurn(false);
        //bankRoll.Bet(bet);
        bets[0] = new Bet(bet, bankRoll);
        bets[MAX_SPLITS] = new Bet(bet, bankRoll);
        hands[0].deal();
        hands[MAX_SPLITS].deal();
        dealer.deal();
        hands[0].deal();
        hands[MAX_SPLITS].deal();
        dealer.deal();
        ops.set(OpFlags.AFTER_DEAL);
        Card one=dealer.get(0);
        Card two=dealer.get(1);
        if( (one.getRank()==1 && isTen(two))
                || (isTen(one) && two.getRank()==1)
                )
        {//bj process
            forAllActive(new DoToOne() {
                void doToOne(int i) {
                    bets[i].Loser();
                    bets[i] = null;
                    hands[i].newHand();
                }
            });
            ops.set(OpFlags.NEW_HAND);
        }
        else
        {//no bj

            splitCheckAndSet();
            activeHandIndex = 0;

        }
        setChanged();
        notifyObservers();
    }

    private void forAllActive(DoToOne o) {
        for (int i = 0; i < NO_OF_HANDS; i++) {
            if (bets[i]!=null)
            {//active hand
                o.doToOne(i);
            }
        }
    }

    public void doubleDown() {
         hit();
        stand();
        hands[activeHandIndex].setDoubled();
        bets[activeHandIndex].doubleBet();
        if (hands[activeHandIndex].getTotal()>21)
        {
            bust();
        }
        activateNextHand();
    }

    private void bust() {
        bets[activeHandIndex].Loser();
        bets[activeHandIndex]=null;
        activateNextHand();
    }


    public void hit() {
        hands[activeHandIndex].deal();
        if (hands[activeHandIndex].getTotal()>21)
        {bust();}
        else
        {
            ops.set(OpFlags.PLAY);
        }
    }

    public static String CardsString(Hand hand, String separator, boolean obscure)
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

    public static String HandToString(Hand hand, boolean obscure) {
        if (hand.size()>0) {
            return ((hand.isSoft() && !obscure)? "SOFT":"    ")+ " "+((obscure)?("  "):(String.format("%1$2s",hand.getTotal()))+ " : ")+ CardsString(hand," ", obscure);
        } else {
            return "";
        }
    }
    public String textDisplay() {
        StringBuilder out = new StringBuilder();
        out.append(bankRoll.toString() + "\n");
        out.append("Dealer: ").append(HandToString(dealer, !isDealersTurn())).append("\n\n");

        for (int i = 0; i < NO_OF_HANDS; i++) {
            if (hands[i].size() == 0) {
                out.append("\n");
            } else {
                out.append(((i==activeHandIndex)?("***"):("   "))).append(HandToString(hands[i],false));
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

        table.hands[0].deal();

        table.hands[Table.MAX_SPLITS].deal();
        table.dealer.deal();
        table.hands[0].deal();
        table.hands[Table.MAX_SPLITS].deal();
        table.dealer.deal();
        System.out.println(table.textDisplay());
    }

    private void evaluate() {


        this.setChanged();
        this.notifyObservers();
    }
    private static  boolean isTen(Card card)
    {
        int rank=card.getRank();
        return ((rank>=10) && (rank<=13));
    }

    public void stand() {
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
            if (activeHandIndex >= NO_OF_HANDS)
                {
                    StartDealerProcessing();
                    break;
                }
            else if (hands[activeHandIndex].size()==0)
            {
                ;
            }
            else
            {
                if (hands[activeHandIndex].size()==1)
                {
                    hands[activeHandIndex].deal();
                }
                ops.set(OpFlags.PRE_HIT);
                splitCheckAndSet();
                break;
            }

        }
        this.setChanged();
        this.notifyObservers();

    }

    private void splitCheckAndSet() {
        if (hands[activeHandIndex].get(0).getRank()==hands[activeHandIndex].get(1).getRank())
        {
            if (activeHandIndex+1!=NO_OF_HANDS || activeHandIndex+1!=MAX_SPLITS)
            {
                EnumSet<OP> newOps=ops.get();
                newOps.add(OP.split);
                ops.set(newOps);
            }
        }
    }

    private void StartDealerProcessing() {
        setDealersTurn(true);
        setChanged();
        notifyObservers();

        while (true)
        {
            if (dealer.getTotal()>22)
            {
                allWin();
                break;
            }
            else if (dealer.getTotal()==22)
            {
                allPush();
                break;
            }
            else if (dealer.getTotal()>17
                    || (dealer.getTotal()==17 && !dealer.isSoft()) )
            {
                standAndEval();
                break;
            }
            else
            {
                dealer.deal();
                setChanged();
                notifyObservers();
            }
        }
        ops.set(OpFlags.NEW_HAND);
        setChanged();
        notifyObservers();
    }

    private void standAndEval() {
        forAllActive(new DoToOne() {
            @Override
            void doToOne(int i) {
                if (dealer.getTotal()==hands[i].getTotal())
                {bets[i].Push();}
                else if (dealer.getTotal()>hands[i].getTotal())
                {
                    bets[i].Loser();
                    bets[i]=null;
                }
                else
                {
                    bets[i].Winner(1,1);
                    bets[i]=null;
                }
            }
        });
    }


    private void allPush() {
        forAllActive(new DoToOne() {
            @Override
            void doToOne(int i) {
                bets[i].Push();
                bets[i]=null;
            }
        });
    }

    private void allWin() {
        forAllActive(new DoToOne() {
            @Override
            void doToOne(int i) {
                bets[i].Winner(1,1);
                bets[i]=null;

            }
        });
    }

    public void setDealersTurn(boolean dealersTurn) {
        this.dealersTurn = dealersTurn;
        setChanged();
        notifyObservers();
    }


    public void swap() {
        Hand.Swap(hands[0], hands[MAX_SPLITS]);
        evaluate();
    }

    public void split() {
    }

    public abstract class DoToOne {
        abstract void  doToOne(int i);
    }
}


