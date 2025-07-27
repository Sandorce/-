package ddz;

import java.util.*;

/**
 * 单机斗地主 —— 最小可运行版
 * 规则：54 张牌，随机发牌，固定用户当地主，AI 按最小单牌策略出牌。
 * 控制台交互：输入 0 表示过牌，输入形如 3 4 5 的索引可出牌。
 */
public class Main {

    /* ===== 数据结构 ===== */
    enum Suit {SPADE, HEART, DIAMOND, CLUB, JOKER}
    enum Rank {
        THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
        JACK, QUEEN, KING, ACE, TWO, JOKER_SMALL, JOKER_BIG
    }
    record Card(Suit suit, Rank rank) implements Comparable<Card> {
        public int value() {               // 3-15 用于比大小
            return rank.ordinal() + 3;
        }
        public String toString() {
            if (rank == Rank.JOKER_SMALL) return "小王";
            if (rank == Rank.JOKER_BIG)   return "大王";
            String[] suitNames = {"♠","♥","♦","♣"};
            String[] rankNames = {"3","4","5","6","7","8","9","10","J","Q","K","A","2"};
            return suitNames[suit.ordinal()] + rankNames[rank.ordinal()];
        }
        public int compareTo(Card o) {     // 升序
            return Integer.compare(value(), o.value());
        }
    }

    /* ===== 工具方法 ===== */
    static List<Card> freshDeck() {
        List<Card> deck = new ArrayList<>();
        for (Suit s : Suit.values()) {
            if (s == Suit.JOKER) continue;
            for (Rank r : Rank.values()) {
                if (r.compareTo(Rank.TWO) <= 0)
                    deck.add(new Card(s, r));
            }
        }
        deck.add(new Card(Suit.JOKER, Rank.JOKER_SMALL));
        deck.add(new Card(Suit.JOKER, Rank.JOKER_BIG));
        return deck;
    }

    /* ===== 玩家 ===== */
    static class Player {
        String name;
        List<Card> hand = new ArrayList<>();
        Player(String name) { this.name = name; }

        void sortHand() { Collections.sort(hand); }

        boolean removeCards(List<Card> cards) {
            if (!hand.containsAll(cards)) return false;
            hand.removeAll(cards);
            return true;
        }

        /** AI 策略：能管就上最小牌型，否则过牌 */
        List<Card> autoPlay(List<Card> last) {
            sortHand();
            if (last == null || last.isEmpty()) { // 首出
                return List.of(hand.get(0));
            }
            int lastVal = last.get(0).value();
            for (Card c : hand) {
                if (c.value() > lastVal) {
                    return List.of(c);
                }
            }
            return List.of(); // 过牌
        }
    }

    /* ===== 游戏主循环 ===== */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        /* 1. 洗牌发牌 */
        List<Card> deck = freshDeck();
        Collections.shuffle(deck, new Random());

        Player user = new Player("你");
        Player ai1   = new Player("AI-1");
        Player ai2   = new Player("AI-2");

        for (int i = 0; i < 51; i += 3) {
            user.hand.add(deck.get(i));
            ai1.hand.add(deck.get(i + 1));
            ai2.hand.add(deck.get(i + 2));
        }
        List<Card> landlordCards = new ArrayList<>(deck.subList(51, 54));
        user.hand.addAll(landlordCards);

        user.sortHand();
        ai1.sortHand();
        ai2.sortHand();

        System.out.println("地主牌：" + landlordCards);
        System.out.println("你是地主，手牌：" + user.hand);

        /* 2. 出牌顺序：user -> ai1 -> ai2 */
        List<Player> table = List.of(user, ai1, ai2);
        List<Card> lastOut = new ArrayList<>();
        int turn = 0;

        while (true) {
            Player p = table.get(turn % 3);
            if (p == user) { // 用户回合
                System.out.print("轮到你，输入出牌索引(空格分隔)或 0 过牌：");
                String line = sc.nextLine().trim();
                List<Card> out = new ArrayList<>();
                if ("0".equals(line)) {
                    out = List.of();
                } else {
                    String[] parts = line.split("\\s+");
                    for (String s : parts) {
                        int idx = Integer.parseInt(s);
                        if (idx < 0 || idx >= user.hand.size()) {
                            System.out.println("索引越界，当跳过");
                            out = List.of();
                            break;
                        }
                        out.add(user.hand.get(idx));
                    }
                }
                if (!out.isEmpty() && !validPlay(out)) {
                    System.out.println("非法牌型，当跳过");
                    out = List.of();
                }
                if (!out.isEmpty() && !lastOut.isEmpty() && !canBeat(out, lastOut)) {
                    System.out.println("管不上，当跳过");
                    out = List.of();
                }
                if (!out.isEmpty()) {
                    user.removeCards(out);
                    lastOut = out;
                    System.out.println("你出了：" + out);
                } else {
                    System.out.println("你过牌");
                }
            } else { // AI 回合
                List<Card> out = p.autoPlay(lastOut);
                if (!out.isEmpty()) {
                    p.removeCards(out);
                    lastOut = out;
                    System.out.println(p.name + " 出了：" + out);
                } else {
                    System.out.println(p.name + " 过牌");
                }
            }

            if (p.hand.isEmpty()) {
                System.out.println(p.name + " 获胜！游戏结束。");
                break;
            }
            turn++;
        }
    }

    /* ===== 规则判断 ===== */
    static boolean validPlay(List<Card> cards) {
        return cards.stream().map(Card::value).distinct().count() == 1; // 只支持单张
    }
    static boolean canBeat(List<Card> now, List<Card> prev) {
        if (now.size() != prev.size()) return false;
        return now.get(0).value() > prev.get(0).value();
    }
}