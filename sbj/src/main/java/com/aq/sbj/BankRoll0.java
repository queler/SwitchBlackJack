package com.aq.sbj;

import java.util.Observable;

/**
 * Created by amq102 on 6/5/2015.
 */
public class BankRoll0 extends Observable {
//
//    private int money;
//
//
//    public int getMoney() {
//        return money;
//    }
//
//    private void setMoney(int money) {
//        this.money = money;
//        hasChanged();
//        notifyObservers();
//    }
//
//    public Bet CreateBet(int betAmount) {
//        Bet newBet = new Bet(betAmount, this);
//        setMoney(getMoney() - betAmount);
//        return newBet;
//    }
//
//    private void takeBet(Bet bet) {
//        setMoney(getMoney() + bet.getMoney() + bet.winAmount);
//    }
//
//    /**
//     * Created by amq102 on 6/5/2015.
//     */
//    public static class Bet extends Observable {
//        int money;
//        BankRoll source;
//        private int winAmount = 0;
//
//        public Bet getExtra() {
//            return extra;
//        }
//
//        public void setExtra(Bet extra) {
//            this.extra = extra;
//            setChanged();
//            notifyObservers();
//        }
//
//        private Bet extra;
//        private Bet(int money, BankRoll source) {
//            this.money = money;
//            this.source = source;
//            setChanged();
//            notifyObservers();
//        }
//
//        private Bet(int money) {
//            this.money = money;
//        }
//
//        public int getMoney() {
//            return money;
//        }
//
//        public void win(int oddsTop, int oddsBottom) {
//            winAmount = getMoney() * oddsTop / oddsBottom;
//            source.takeBet(this);
//            if (extra != null) {
//                extra.win();
//            }
//        }
//
//        public void win() {
//            win(1, 1);
//        }
//    }
//

}

