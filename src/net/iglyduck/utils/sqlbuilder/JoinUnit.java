package net.iglyduck.utils.sqlbuilder;

/*
 Copyright (c) 2015 aglyduck
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * 联接模块
 * 用于生成  inner join 子句  或  left join 子句
 * 
 * new JoinUnit().init( ... ).left( ... ).toString();
 * new JoinUnit().init( ... ).inner( ... ).toString();
 * 
 * @author aglyduck
 */
public class JoinUnit {
    private final static class JoinItem {
        private String join;
        private Object item;
        private ExpressUnit on;
        private JoinItem next;

        public JoinItem() {
        }

        public JoinItem(String join) {
            this.join = join;
        }

        public JoinItem item(Object item) {
            this.item = item;
            return this;
        }

        public JoinItem on(ExpressUnit on) {
            this.on = on;
            return this;
        }

        public JoinItem getNext() {
            return next;
        }

        public void setNext(JoinItem next) {
            this.next = next;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();

            if (StringUtils.isNotEmpty(join)) {
                s.append(" ").append(join).append(" ");
            }

            s.append(item.toString());

            if (on != null) {
                s.append(" on ").append(on.toString());
            }

            return s.toString();
        }
    }

    private JoinItem head;
    private JoinItem tail;

    public boolean isEmpty() {
        return head == null;
    }

    public JoinUnit init(WrapTable t) {
        Validate.notNull(t);
        
        JoinItem item = new JoinItem().item(t);

        head = item;
        tail = item;

        return this;
    }
    
    protected JoinUnit join(String join, WrapTable t, ExpressUnit on) {
        Validate.notEmpty(join);
        Validate.notNull(t);
        Validate.notNull(head);
        
        JoinItem item = new JoinItem(join).item(t).on(on);

        tail.setNext(item);
        tail = item;

        return this;
    }

    public JoinUnit inner(WrapTable t, ExpressUnit on) {
        return join("inner join", t, on);
    }
    
    public JoinUnit inner(WrapTable t) {
        return join("inner join", t, null);
    }
    
    public JoinUnit left(WrapTable t, ExpressUnit on) {
        return join("left join", t, on);
    }

    public JoinUnit left(WrapTable t) {
        return join("left join", t, null);
    }
    
    @Override
    public String toString() {
        Validate.isTrue(head != tail);

        StringBuilder s = new StringBuilder();

        JoinItem item = head;
        s.append(item.toString());
        while (item.hasNext()) {
            item = item.getNext();
            s.append(item.toString());
        }

        return s.toString();
    }
}
