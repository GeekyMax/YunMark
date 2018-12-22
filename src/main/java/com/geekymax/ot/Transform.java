package com.geekymax.ot;

import com.google.common.collect.TreeMultimap;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Stas on 3/15/16.
 */
public class Transform {
    private Transform() {
    }

    private static void transform(Retain r1, Retain r2, ResultBuilder builder) {
        if (r1.offset() == r2.offset()) {
            builder.addLeft(r1);
            builder.addRight(r2);
        } else if (r1.offset() > r2.offset()) {
            builder.replaceLeft(new Retain(r1.offset() - r2.offset()));
            builder.addLeft(new Retain(r2.offset()));
            builder.addRight(r2);
        } else if (r1.offset() < r2.offset()) {
            builder.replaceRight(new Retain(r2.offset() - r1.offset()));
            builder.addLeft(r1);
            builder.addRight(new Retain(r1.offset()));
        }
    }

    private static void transform(Retain r1, Insert i2, ResultBuilder builder) {
        builder.addLeft(new Retain(r1.offset() + i2.offset()));
        builder.addRight(i2);
    }

    private static void transform(Retain r1, Delete d2, ResultBuilder builder) {
        if (r1.offset() == d2.len) {
            builder.addRight(d2);
        } else if (r1.offset() > d2.len) {
            builder.replaceLeft(new Retain(r1.offset() - d2.len));
            builder.addRight(d2);
        } else if (r1.offset() < d2.len) {
            builder.addRight(new Delete(r1.offset()));
            builder.replaceRight(new Delete(d2.len - r1.offset()));
        }
    }

    private static void transform(Insert i1, Delete d2, ResultBuilder builder) {
        builder.addLeft(i1);
        builder.addRight(new Retain(i1.offset()));
        builder.addRight(d2);
    }

    private static void transform(Insert i1, Insert i2, ResultBuilder builder) {
        if (i1.revision() <= i2.revision()) { //fixme: rev1 == rev2 -should not be possible
            builder.addLeft(i1);
            builder.addLeft(new Retain(i2.offset()));
            builder.addRight(new Retain(i1.offset()));
            builder.addRight(i2);
        } else {
            builder.addLeft(new Retain(i2.offset()));
            builder.addLeft(i1);
            builder.addRight(i2);
            builder.addRight(new Retain(i1.offset()));
        }
    }

    private static void transform(Delete d1, Delete d2, ResultBuilder builder) {
        if (d1.len > d2.len) {
            builder.replaceLeft(new Delete(d1.len - d2.len));
        } else if (d1.len < d2.len) {
            builder.replaceRight(new Delete(d2.len - d1.len));
        }
    }

    public static Result transform(Changes chs1, Changes chs2) {
        ResultBuilder builder = new ResultBuilder(chs1.changes, chs2.changes);
        while (builder.hasNext()) {
            Change ch1 = builder.left();
            Change ch2 = builder.right();
            if (ch1 instanceof Retain) {
                if (ch2 instanceof Retain) {
                    transform((Retain) ch1, (Retain) ch2, builder);
                }
                if (ch2 instanceof Insert) {
                    transform((Retain) ch1, (Insert) ch2, builder);
                }
                if (ch2 instanceof Delete) {
                    transform((Retain) ch1, (Delete) ch2, builder);
                }
            } else if (ch1 instanceof Insert) {
                if (ch2 instanceof Retain) {
                    transform((Retain) ch2, (Insert) ch1, builder.flip());
                } else if (ch2 instanceof Insert) {
                    transform((Insert) ch1, (Insert) ch2, builder);
                } else if (ch2 instanceof Delete) {
                    transform((Insert) ch1, (Delete) ch2, builder);
                }
            } else if (ch1 instanceof Delete) {
                if (ch2 instanceof Retain) {
                    transform((Retain) ch2, (Delete) ch1, builder.flip());
                } else if (ch2 instanceof Insert) {
                    transform((Insert) ch2, (Delete) ch1, builder.flip());
                } else if (ch2 instanceof Delete) {
                    transform((Delete) ch1, (Delete) ch2, builder);
                }
            }
        }

        return builder.build();
    }

    static void transformMarkupAgainstInsert(TreeMultimap<Integer, Markup> markup, int insPos, int insTxtLen) {
        List<Integer> list = newArrayList((markup.asMap().tailMap(insPos).keySet()));
        for (int i = list.size() - 1; i >= 0; i--) {
            int newPos = list.get(i) + insTxtLen;
            SortedSet<Markup> m = markup.removeAll(list.get(i));
            m.forEach(e -> e.fireShift(insTxtLen, newPos));
            markup.putAll(newPos, m);
        }
    }

    static void transformMarkupAgainstDelete(TreeMultimap<Integer, Markup> markup, int delPos, int delTextLen) {
        for (Integer p : newArrayList((markup.asMap().tailMap(delPos, false).keySet()))) {
            Set<Markup> m = markup.removeAll(p);
            int d = p >= delPos + delTextLen ? -delTextLen : delPos - p;
            m.forEach(e -> e.fireShift(d, p + d));
            markup.putAll(p + d, m);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ResultBuilder {
        private ListIterator<Change> leftIterator;
        private ListIterator<Change> rightIterator;
        private List<Change> leftRes;
        private List<Change> rightRes;
        boolean flipped = false;

        public ResultBuilder(List<Change> l, List<Change> r) {
            this.leftIterator = new ArrayList<>(l).listIterator();
            this.rightIterator = new ArrayList<>(r).listIterator();
            this.leftRes = new ArrayList<>(l.size());
            this.rightRes = new ArrayList<>(r.size());
        }

        boolean hasNext() {
            return leftIterator.hasNext() && rightIterator.hasNext();
        }

        Change left() {
            return leftIterator.next();
        }

        Change right() {
            return rightIterator.next();
        }

        void addLeft(Change c) {
            leftRes.add(c);
        }

        void addRight(Change c) {
            rightRes.add(c);
        }

        void replaceLeft(Change c) {
            leftIterator.set(c);
            leftIterator.previous();
        }

        void replaceRight(Change c) {
            rightIterator.set(c);
            rightIterator.previous();
        }

        ResultBuilder flip() {
            flipped = !flipped;

            List<Change> b = this.leftRes;
            this.leftRes = this.rightRes;
            this.rightRes = b;

            ListIterator<Change> i = this.leftIterator;
            this.leftIterator = this.rightIterator;
            this.rightIterator = i;

            return this;
        }

        Result build() {
            while (leftIterator.hasNext()) {
                leftRes.add(leftIterator.next());
            }
            while (rightIterator.hasNext()) {
                rightRes.add(rightIterator.next());
            }
            return flipped ? new Result(rightRes, leftRes) : new Result(leftRes, rightRes);
        }
    }

    public static class Result {
        private final Changes changes1;
        private final Changes changes2;

        public Result(List<Change> changes1, List<Change> changes2) {
            this.changes1 = new Changes(changes1);
            this.changes2 = new Changes(changes2);
        }

        public Change getLeft() {
            return changes1;
        }

        public Change getRight() {
            return changes2;
        }
    }

}
