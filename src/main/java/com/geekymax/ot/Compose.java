package com.geekymax.ot;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.max;

/**
 *
 * @author Stas
 * @date 3/21/16
 */
public class Compose {
    private Compose() {
    }

    public static Changes compose(Changes l, Changes r) {
        ListIterator<AbstractChange> i = new ArrayList<>(l.changes).listIterator();
        ListIterator<AbstractChange> j = new ArrayList<>(r.changes).listIterator();
        List<AbstractChange> result = new ArrayList<>(max(l.changes.size(), r.changes.size()));
        while (i.hasNext() && j.hasNext()) {
            AbstractChange ch1 = i.next();
            AbstractChange ch2 = j.next();
            if (ch1 instanceof Retain) {
                if (ch2 instanceof Retain) {
                    if (ch1.offset() == ch2.offset()) {
                        result.add(ch1);
                    } else if (ch1.offset() < ch2.offset()) {
                        result.add(ch1);
                        j.set(new Retain(ch2.offset() - ch1.offset()));
                        j.previous();
                    } else if (ch1.offset() > ch2.offset()) {
                        result.add(ch2);
                        i.set(new Retain(ch1.offset() - ch2.offset()));
                        i.previous();
                    }
                } else if (ch2 instanceof Insert) {
                    result.add(ch2);
                    i.set(new Retain(ch1.offset() - ch2.offset()));
                    i.previous();
                } else if (ch2 instanceof Delete) {
                    result.add(ch2);
                    int len = ((Delete) ch2).len;
                    if (len < ch1.offset()) {
                        i.set(new Retain(ch1.offset() - len));
                        i.previous();
                    }
                }
            } else if (ch1 instanceof Delete) {
                if (ch2 instanceof Retain) {
                    result.add(ch1);
                    j.previous();
                } else if (ch2 instanceof Insert) {
                    result.add(ch1);
                    result.add(ch2);
                } else if (ch2 instanceof Delete) {
                    result.add(ch1);
                    j.previous();
                }
            } else if (ch1 instanceof Insert) {
                if (ch2 instanceof Retain) {
                    result.add(ch1);
                    j.set(new Retain(ch2.offset() - ch1.offset()));
                    j.previous();
                } else if (ch2 instanceof Insert) {
                    if (ch1.revision() <= ch2.revision()) {
                        result.add(new Insert(((Insert) ch1).text + ((Insert) ch2).text));
                    } else {
                        result.add(new Insert(((Insert) ch2).text + ((Insert) ch1).text));
                    }
                } else if (ch2 instanceof Delete) {
                    Insert i1 = (Insert) ch1;
                    Delete d2 = (Delete) ch2;
                    if (i1.offset() > d2.len) {
                        result.add(new Insert(i1.text.substring(d2.len)));
                    }
                }
            }
        }

        while (i.hasNext()) {
            result.add(i.next());
        }
        while (j.hasNext()) {
            result.add(j.next());
        }

        return new Changes(result);
    }
}
